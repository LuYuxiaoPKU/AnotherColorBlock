package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
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

        modBus.addListener(RegisterPayloadHandlersEvent.class, this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
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
