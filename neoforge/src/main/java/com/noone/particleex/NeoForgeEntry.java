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
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mod(NetworkIdentifiers.MOD_ID)
public class NeoForgeEntry {
    /** 20.3-beta 老 SimpleChannel API（payload 注册表体系 20.4 才引入）——同 1.20.1 模式。 */
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NetworkIdentifiers.MOD_ID, "main"),
            () -> "1",
            s -> true,
            s -> true);

    public NeoForgeEntry(IEventBus modBus) {
        try {
            ParticleExConfig.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 平台桥注入（发送 + 错误上报）
        Bridge.setSender((world, payload) -> {
            CHANNEL.send(PacketDistributor.DIMENSION.with(() -> world.dimension()), payload);
        });
        if (FMLEnvironment.dist.isClient()) {
            MessageBridge.setSink(new ClientMessageUtil());
        }

        registerMessages();
        registerArgumentTypes(modBus);
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
     * 1.20.6 同走 RegisterEvent（20.6 有该事件；直塞另有 HolderOwner ClassCastException
     * 问题——20.6 的 MappedRegistry 不实现 HolderOwner，须走 holderOwner()）。
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

        // RegisterEvent（mod bus）注册：字段直塞只动 byId/toId 两套结构，RegistrySnapshot 从 keySet 派生会丢条目；
        // 且 1.20.6 直塞 createIntrusive(registry,...) 直接 ClassCastException（20.6 的 MappedRegistry 不实现 HolderOwner）
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
                Registry.register((Registry) event.getRegistry(), new ResourceLocation(NetworkIdentifiers.MOD_ID, names[i]), own.get(i));
            }
        });
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ParticleExCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    private void registerMessages() {
        CHANNEL.messageBuilder(ClearParticlePayload.class, 0)
                .decoder(ClearParticlePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ClearParticlePayload>clientOnly((p) -> ClientNetworkHandler.clearParticle()))
                .add();
        CHANNEL.messageBuilder(ClearCachePayload.class, 1)
                .decoder(ClearCachePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ClearCachePayload>clientOnly((p) -> ClientNetworkHandler.clearCache()))
                .add();
        CHANNEL.messageBuilder(NormalPayload.class, 2)
                .decoder(NormalPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<NormalPayload>clientOnly(ClientNetworkHandler::normal))
                .add();
        CHANNEL.messageBuilder(ConditionalPayload.class, 3)
                .decoder(ConditionalPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ConditionalPayload>clientOnly(ClientNetworkHandler::conditional))
                .add();
        CHANNEL.messageBuilder(ParameterPayload.class, 4)
                .decoder(ParameterPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ParameterPayload>clientOnly(ClientNetworkHandler::parameter))
                .add();
        CHANNEL.messageBuilder(ImagePayload.class, 5)
                .decoder(ImagePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ImagePayload>clientOnly(ClientNetworkHandler::image))
                .add();
        CHANNEL.messageBuilder(ImageMatrixPayload.class, 6)
                .decoder(ImageMatrixPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ImageMatrixPayload>clientOnly(ClientNetworkHandler::imageMatrix))
                .add();
        CHANNEL.messageBuilder(VideoPayload.class, 7)
                .decoder(VideoPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<VideoPayload>clientOnly(ClientNetworkHandler::video))
                .add();
        CHANNEL.messageBuilder(VideoMatrixPayload.class, 8)
                .decoder(VideoMatrixPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<VideoMatrixPayload>clientOnly(ClientNetworkHandler::videoMatrix))
                .add();
        CHANNEL.messageBuilder(GroupRemovePayload.class, 9)
                .decoder(GroupRemovePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<GroupRemovePayload>clientOnly(ClientNetworkHandler::groupRemove))
                .add();
        CHANNEL.messageBuilder(GroupChangePayload.class, 10)
                .decoder(GroupChangePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<GroupChangePayload>clientOnly(ClientNetworkHandler::groupChange))
                .add();
    }

    /** 20.3-beta 老 API：仅客户端处理 S2C（服务端收到忽略） */
    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> clientOnly(java.util.function.Consumer<T> handler) {
        return (payload, ctx) -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                handler.accept(payload);
            }
        };
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
