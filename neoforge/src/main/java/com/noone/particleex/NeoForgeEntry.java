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
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.IIPayloadRegistrar;
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
        MessageBridge.setSink(new ClientMessageUtil());

        modBus.addListener(RegisterPayloadHandlerEvent.class, this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ParticleExCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    private void registerPayloads(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(NetworkIdentifiers.MOD_ID).versioned("1");
        reg(registrar, ClearParticlePayload.TYPE, ClearParticlePayload::read, (p, ctx) -> ClientNetworkHandler.clearParticle(ctx));
        reg(registrar, ClearCachePayload.TYPE, ClearCachePayload::read, (p, ctx) -> ClientNetworkHandler.clearCache(ctx));
        reg(registrar, NormalPayload.TYPE, NormalPayload::read, ClientNetworkHandler::normal);
        reg(registrar, ConditionalPayload.TYPE, ConditionalPayload::read, ClientNetworkHandler::conditional);
        reg(registrar, ParameterPayload.TYPE, ParameterPayload::read, ClientNetworkHandler::parameter);
        reg(registrar, ImagePayload.TYPE, ImagePayload::read, ClientNetworkHandler::image);
        reg(registrar, ImageMatrixPayload.TYPE, ImageMatrixPayload::read, ClientNetworkHandler::imageMatrix);
        reg(registrar, VideoPayload.TYPE, VideoPayload::read, ClientNetworkHandler::video);
        reg(registrar, VideoMatrixPayload.TYPE, VideoMatrixPayload::read, ClientNetworkHandler::videoMatrix);
        reg(registrar, GroupRemovePayload.TYPE, GroupRemovePayload::read, ClientNetworkHandler::groupRemove);
        reg(registrar, GroupChangePayload.TYPE, GroupChangePayload::read, ClientNetworkHandler::groupChange);
    }

    /** 注册 S2C payload：handler 包 enqueueWork 切回主线程（NeoForge 网络线程要求） */
    private static <T extends CustomPacketPayload> void reg(
            IPayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            FriendlyByteBuf.Reader<T> reader,
            BiConsumer<T, IPayloadContext> handler) {
        registrar.play(type.id(), reader, (payload, ctx) -> ctx.enqueueWork(() -> handler.accept(payload, ctx)));
    }
}
