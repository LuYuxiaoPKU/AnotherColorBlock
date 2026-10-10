package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.Color4ArgumentType;
import com.noone.particleex.command.argument.FlipArgumentType;
import com.noone.particleex.command.argument.GroupChangeTypeArgumentType;
import com.noone.particleex.command.argument.Range3ArgumentType;
import com.noone.particleex.command.argument.RotateArgumentType;
import com.noone.particleex.command.argument.Speed3ArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.noone.particleex.command.argument.SuggestDoubleArgumentType;
import com.noone.particleex.command.argument.SuggestIntegerArgumentType;
import com.noone.particleex.mixin.ArgumentTypeInfosAccessor;
import com.noone.particleex.mixin.MappedRegistryAccessor;
import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.ClientNetworkHandler;
import com.noone.particleex.network.NetworkIdentifiers;
import com.noone.particleex.network.payload.ClearCachePayload;
import com.noone.particleex.network.payload.ClearParticlePayload;
import com.noone.particleex.network.payload.ConditionalPayload;
import com.noone.particleex.network.payload.GroupChangePayload;
import com.noone.particleex.network.payload.GroupRemovePayload;
import com.noone.particleex.network.payload.ImageMatrixPayload;
import com.noone.particleex.network.payload.ImagePayload;
import com.noone.particleex.network.payload.NormalPayload;
import com.noone.particleex.network.payload.ParameterPayload;
import com.noone.particleex.network.payload.VideoMatrixPayload;
import com.noone.particleex.network.payload.VideoPayload;
import com.noone.particleex.util.ClientMessageUtil;
import com.noone.particleex.util.MessageBridge;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

@Mod(NetworkIdentifiers.MOD_ID)
public class NeoForgeEntry {
    public NeoForgeEntry(IEventBus modBus) {
        try {
            ParticleExConfig.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 平台桥注入（发送 + 错误上报）
        Bridge.setSender((world, payload) -> PacketDistributor.sendToPlayersInDimension(world, payload));
        if (FMLEnvironment.dist.isClient()) {
            MessageBridge.setSink(new ClientMessageUtil());
        }

        registerArgumentTypes();
        modBus.addListener(RegisterPayloadHandlersEvent.class, this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    /**
     * 注册自定义命令参数类型（与 Fabric 端 ParticleEx.onInitialize 的
     * ArgumentTypeRegistry.registerArgumentType 对等，同款 BY_CLASS 直塞）。
     * 不注册的话，服务端下发命令树时 ArgumentTypeInfos.unpack/byClass 抛
     * IllegalArgumentException（Unrecognized argument type）→ 进档失败。
     * 字节码实证：
     *  - 26.x 无公开 register；BuiltInRegistries.COMMAND_ARGUMENT_TYPE 在
     *    neoforge mod 构造期已冻结（Registry.register 抛 IllegalStateException）；
     *  - ClientboundCommandsPacket 写侧 serializeCap 写 getId(info)、读侧 byId(id)
     *    查注册表——仅塞 BY_CLASS 会让未注册 info 拿默认 id 0（bool），数据流错位；
     *  - MappedRegistry.freeze() 只置标志不锁 byId/toId 集合——字段级直塞等价
     *    fabric 未冻结时的 Registry.register。
     */
    private void registerArgumentTypes() {
        Map<Class<?>, ArgumentTypeInfo<?, ?>> byClass = ArgumentTypeInfosAccessor.particleex$getByClass();
        byClass.put(Color4ArgumentType.class, SingletonArgumentInfo.contextFree(Color4ArgumentType::color4));
        byClass.put(FlipArgumentType.class, SingletonArgumentInfo.contextFree(FlipArgumentType::flip));
        byClass.put(GroupChangeTypeArgumentType.class, SingletonArgumentInfo.contextFree(GroupChangeTypeArgumentType::type));
        byClass.put(Range3ArgumentType.class, SingletonArgumentInfo.contextFree(Range3ArgumentType::range3));
        byClass.put(RotateArgumentType.class, SingletonArgumentInfo.contextFree(RotateArgumentType::rotate));
        byClass.put(Speed3ArgumentType.class, SingletonArgumentInfo.contextFree(Speed3ArgumentType::speed3));
        byClass.put(SuggestArgumentType.class, SuggestArgumentType.Serializer.INSTANCE);
        byClass.put(SuggestDoubleArgumentType.class, SuggestDoubleArgumentType.Serializer.INSTANCE);
        byClass.put(SuggestIntegerArgumentType.class, SuggestIntegerArgumentType.Serializer.INSTANCE);

        // 注册表直塞（见上方注释）：发包协议 serializeCap 写 getId(info)、收包 byId(id) 查注册表
        Registry<ArgumentTypeInfo<?, ?>> registry = BuiltInRegistries.COMMAND_ARGUMENT_TYPE;
        if (!(registry instanceof MappedRegistry)) {
            throw new IllegalStateException("COMMAND_ARGUMENT_TYPE 不是 MappedRegistry，无法注册参数类型");
        }
        @SuppressWarnings({"rawtypes", "unchecked"})
        MappedRegistryAccessor regAccessor = (MappedRegistryAccessor) (Object) registry;
        ObjectList<Holder.Reference<Object>> byId = (ObjectList) regAccessor.particleex$getById();
        Reference2IntMap<Object> toId = (Reference2IntMap) regAccessor.particleex$getToId();
        for (ArgumentTypeInfo<?, ?> info : byClass.values()) {
            int id = byId.size();
            byId.add((Holder.Reference<Object>) (Object) Holder.Reference.createIntrusive((HolderOwner<Object>) (Object) registry, info));
            toId.put(info, id);
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ParticleExCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NetworkIdentifiers.MOD_ID).versioned("1");
        reg(registrar, ClearParticlePayload.TYPE, ClearParticlePayload.CODEC, (p, ctx) -> ClientNetworkHandler.clearParticle(ctx));
        reg(registrar, ClearCachePayload.TYPE, ClearCachePayload.CODEC, (p, ctx) -> ClientNetworkHandler.clearCache(ctx));
        reg(registrar, NormalPayload.TYPE, NormalPayload.CODEC, ClientNetworkHandler::normal);
        reg(registrar, ConditionalPayload.TYPE, ConditionalPayload.CODEC, ClientNetworkHandler::conditional);
        reg(registrar, ParameterPayload.TYPE, ParameterPayload.CODEC, ClientNetworkHandler::parameter);
        reg(registrar, ImagePayload.TYPE, ImagePayload.CODEC, ClientNetworkHandler::image);
        reg(registrar, ImageMatrixPayload.TYPE, ImageMatrixPayload.CODEC, ClientNetworkHandler::imageMatrix);
        reg(registrar, VideoPayload.TYPE, VideoPayload.CODEC, ClientNetworkHandler::video);
        reg(registrar, VideoMatrixPayload.TYPE, VideoMatrixPayload.CODEC, ClientNetworkHandler::videoMatrix);
        reg(registrar, GroupRemovePayload.TYPE, GroupRemovePayload.CODEC, ClientNetworkHandler::groupRemove);
        reg(registrar, GroupChangePayload.TYPE, GroupChangePayload.CODEC, ClientNetworkHandler::groupChange);
    }

    /** 注册 S2C payload：handler 包 enqueueWork 切回主线程（NeoForge 网络线程要求） */
    private static <T extends CustomPacketPayload> void reg(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, IPayloadContext> handler) {
        registrar.playToClient(type, codec, (payload, ctx) -> ctx.enqueueWork(() -> handler.accept(payload, ctx)));
    }
}
