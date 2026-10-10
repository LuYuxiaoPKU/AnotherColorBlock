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
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;
import java.util.function.BiConsumer;

@Mod(NetworkIdentifiers.MOD_ID)
public class NeoForgeEntry {
    public NeoForgeEntry(IEventBus modBus) {
        try {
            ParticleExConfig.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 平台桥注入（发送 + 错误上报）：错误上报仅客户端（服务器端无 net.minecraft.client 类）
        Bridge.setSender((world, payload) -> PacketDistributor.sendToPlayersInDimension(world, payload));
        if (FMLEnvironment.getDist().isClient()) {
            MessageBridge.setSink(new ClientMessageUtil());
        }

        registerArgumentTypes();
        modBus.addListener(RegisterPayloadHandlersEvent.class, this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    /**
     * 注册自定义命令参数类型（与 Fabric 端 ParticleEx.onInitialize 的
     * ArgumentTypeRegistry.registerArgumentType 对等）。
     * 不注册的话，服务端下发命令树时客户端 ArgumentTypeInfos.byClass 抛
     * IllegalArgumentException（Unrecognized argument type）→ 进档失败。
     * 字节码实证：26.x 无 ArgumentTypeInfos.register，注册表驱动
     * （BuiltInRegistries.COMMAND_ARGUMENT_TYPE + Registry.register，Fabric API 同款）。
     */
    private void registerArgumentTypes() {
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "color4"), SingletonArgumentInfo.contextFree(Color4ArgumentType::color4));
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "flip"), SingletonArgumentInfo.contextFree(FlipArgumentType::flip));
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "group_change"), SingletonArgumentInfo.contextFree(GroupChangeTypeArgumentType::type));
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "range3"), SingletonArgumentInfo.contextFree(Range3ArgumentType::range3));
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "rotate"), SingletonArgumentInfo.contextFree(RotateArgumentType::rotate));
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "speed3"), SingletonArgumentInfo.contextFree(Speed3ArgumentType::speed3));
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "suggest_string"), SuggestArgumentType.Serializer.INSTANCE);
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "suggest_double"), SuggestDoubleArgumentType.Serializer.INSTANCE);
        Registry.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, Identifier.fromNamespaceAndPath(NetworkIdentifiers.MOD_ID, "suggest_integer"), SuggestIntegerArgumentType.Serializer.INSTANCE);
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
