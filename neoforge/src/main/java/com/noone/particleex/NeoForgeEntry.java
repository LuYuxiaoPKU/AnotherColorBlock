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
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
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
        Bridge.setSender((world, payload) -> {
            PacketDistributor.DIMENSION.with(world.dimension()).send(payload);
        });
        if (FMLEnvironment.dist.isClient()) {
            MessageBridge.setSink(new ClientMessageUtil());
        }

        modBus.addListener(RegisterPayloadHandlerEvent.class, this::registerPayloads);
        registerArgumentTypes();
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    /**
     * 注册自定义命令参数类型（1.20.1~1.20.4 同步类名 ArgumentTypeInfos，同 26.2 修复：
     * BY_CLASS + 注册表直塞；MappedRegistry.toId 类型按版本取 Object2IntMap/Reference2IntMap）。
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

    private void registerPayloads(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(NetworkIdentifiers.MOD_ID).versioned("1");
        reg(registrar, NetworkIdentifiers.CLEAR_PARTICLE_PACKET_ID, ClearParticlePayload::read, (p, ctx) -> ClientNetworkHandler.clearParticle(ctx));
        reg(registrar, NetworkIdentifiers.CLEAR_CACHE_PACKET_ID, ClearCachePayload::read, (p, ctx) -> ClientNetworkHandler.clearCache(ctx));
        reg(registrar, NetworkIdentifiers.NORMAL_PACKET_ID, NormalPayload::read, ClientNetworkHandler::normal);
        reg(registrar, NetworkIdentifiers.CONDITIONAL_PACKET_ID, ConditionalPayload::read, ClientNetworkHandler::conditional);
        reg(registrar, NetworkIdentifiers.PARAMETER_PACKET_ID, ParameterPayload::read, ClientNetworkHandler::parameter);
        reg(registrar, NetworkIdentifiers.IMAGE_PACKET_ID, ImagePayload::read, ClientNetworkHandler::image);
        reg(registrar, NetworkIdentifiers.IMAGE_MATRIX_PACKET_ID, ImageMatrixPayload::read, ClientNetworkHandler::imageMatrix);
        reg(registrar, NetworkIdentifiers.VIDEO_PACKET_ID, VideoPayload::read, ClientNetworkHandler::video);
        reg(registrar, NetworkIdentifiers.VIDEO_MATRIX_PACKET_ID, VideoMatrixPayload::read, ClientNetworkHandler::videoMatrix);
        reg(registrar, NetworkIdentifiers.GROUP_REMOVE_PACKET_ID, GroupRemovePayload::read, ClientNetworkHandler::groupRemove);
        reg(registrar, NetworkIdentifiers.GROUP_CHANGE_PACKET_ID, GroupChangePayload::read, ClientNetworkHandler::groupChange);
    }

    /** 注册 S2C payload：handler 包 enqueueWork 切回主线程（NeoForge 网络线程要求） */
    private static <T extends CustomPacketPayload> void reg(
            IPayloadRegistrar registrar,
            ResourceLocation id,
            net.minecraft.network.FriendlyByteBuf.Reader<T> reader,
            BiConsumer<T, IPayloadContext> handler) {
        registrar.play(id, reader, (payload, ctx) -> ctx.workHandler().execute(() -> handler.accept(payload, ctx)));
    }
}
