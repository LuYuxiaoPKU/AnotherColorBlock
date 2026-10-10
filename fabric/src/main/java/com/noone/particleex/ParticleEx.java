package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.*;
import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.payload.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ParticleEx implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("particleex");
    public static final String MOD_ID = "particleex";

    @Override
    public void onInitialize() {
        try {
            ParticleExConfig.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 平台桥：命令层发送 → Fabric 网络发送；错误上报 → 客户端聊天框
        Bridge.setSender((world, payload) -> PlayerLookup.world(world).forEach(player -> ServerPlayNetworking.send(player, payload)));
        registerPayloads();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> ParticleExCommand.register(dispatcher, registryAccess));
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(ClearParticlePayload.TYPE, ClearParticlePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClearCachePayload.TYPE, ClearCachePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(NormalPayload.TYPE, NormalPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConditionalPayload.TYPE, ConditionalPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ParameterPayload.TYPE, ParameterPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ImagePayload.TYPE, ImagePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ImageMatrixPayload.TYPE, ImageMatrixPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(VideoPayload.TYPE, VideoPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(VideoMatrixPayload.TYPE, VideoMatrixPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GroupRemovePayload.TYPE, GroupRemovePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GroupChangePayload.TYPE, GroupChangePayload.CODEC);
    }
}