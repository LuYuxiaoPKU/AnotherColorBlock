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
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
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

        registerArgumentTypes(modBus);
        modBus.addListener(RegisterPayloadHandlersEvent.class, this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    /**
     * 注册自定义命令参数类型（与 Fabric 端 ParticleEx.onInitialize 的
     * ArgumentTypeRegistry.registerArgumentType 对等）。
     * 不注册的话，服务端下发命令树时 ArgumentTypeInfos.unpack/byClass 抛
     * IllegalArgumentException（Unrecognized argument type）→ 进档失败。
     *
     * 1.21.4 进档崩溃（commands 包 VarInt too big）排查结论（七轮探针实证，2026-10-14）：
     *  - 字段级直塞 byId/toId（MappedRegistryAccessor）在写侧（服务端）全部健康
     *    （roundtrip=true、holeScan=0、registry 实例同一、SERIALIZE 字节正常），
     *    但客户端读包线程 byId(typeId) 返回 null（stub=NULL 铁证）→
     *    serializer 数据未消费 → 字节流错位 → VarInt too big 进档即断；
     *  - mod 构造期 Registry.register 抛 IllegalStateException（Registry is already
     *    frozen）——NeoForge 1.21.4 构造期不在 unfreeze 窗口；
     *  - 正路 = RegisterEvent（NeoForge 官方注册窗口，mod bus 事件，解冻期内触发），
     *    同步维护 byId/toId/byValue/byKey 全套内部结构与 NeoForge 快照管理。
     * 26.x 无 RegisterEvent 可用窗口时维持字段直塞方案（其协议只查 BY_CLASS）。
     */
    private void registerArgumentTypes(IEventBus modBus) {
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

        // RegisterEvent（mod bus）注册：字段直塞只动 byId/toId 两套结构，RegistrySnapshot 从 keySet 派生会丢条目，
        // 客户端进档 FrozenRegistrySync applySnapshot 读侧 byId=null → 字节流错位 → VarInt too big（实证见 test/neoforge-argtype-register-research.md）
        String[] names = {"color4", "flip", "group_change_type", "range3", "rotate", "speed3",
            "suggest", "suggest_double", "suggest_integer"};
        modBus.addListener(RegisterEvent.class, event -> {
            if (!BuiltInRegistries.COMMAND_ARGUMENT_TYPE.key().equals(event.getRegistryKey())) {
                return;
            }
            List<ArgumentTypeInfo<?, ?>> own = List.of(
                byClass.get(Color4ArgumentType.class),
                byClass.get(FlipArgumentType.class),
                byClass.get(GroupChangeTypeArgumentType.class),
                byClass.get(Range3ArgumentType.class),
                byClass.get(RotateArgumentType.class),
                byClass.get(Speed3ArgumentType.class),
                byClass.get(SuggestArgumentType.class),
                byClass.get(SuggestDoubleArgumentType.class),
                byClass.get(SuggestIntegerArgumentType.class)
            );
            for (int i = 0; i < names.length; i++) {
                if (own.get(i) == null) {
                    throw new IllegalStateException("自定义参数类型 info 为 null，BY_CLASS put 未生效: " + names[i]);
                }
                //noinspection unchecked,rawtypes
                Registry.register((Registry) event.getRegistry(), ResourceLocation.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, names[i]), own.get(i));
            }
        });
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
