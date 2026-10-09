package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.*;
import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.payload.*;
import com.noone.particleex.util.ClientMessageUtil;
import com.noone.particleex.util.MessageBridge;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.Identifier;
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
        Bridge.setSender((world, payload) -> PlayerLookup.level(world).forEach(player -> ServerPlayNetworking.send(player, payload)));
        MessageBridge.setSink(new ClientMessageUtil());
        registerPayloads();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> ParticleExCommand.register(dispatcher, registryAccess));
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "color4"), Color4ArgumentType.class, SingletonArgumentInfo.contextFree(Color4ArgumentType::color4));
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "flip"), FlipArgumentType.class, SingletonArgumentInfo.contextFree(FlipArgumentType::flip));
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "group_change"), GroupChangeTypeArgumentType.class, SingletonArgumentInfo.contextFree(GroupChangeTypeArgumentType::type));
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "range3"), Range3ArgumentType.class, SingletonArgumentInfo.contextFree(Range3ArgumentType::range3));
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "rotate"), RotateArgumentType.class, SingletonArgumentInfo.contextFree(RotateArgumentType::rotate));
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "speed3"), Speed3ArgumentType.class, SingletonArgumentInfo.contextFree(Speed3ArgumentType::speed3));
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "suggest_string"), SuggestArgumentType.class, SuggestArgumentType.Serializer.INSTANCE);
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "suggest_double"), SuggestDoubleArgumentType.class, SuggestDoubleArgumentType.Serializer.INSTANCE);
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath(MOD_ID, "suggest_integer"), SuggestIntegerArgumentType.class, SuggestIntegerArgumentType.Serializer.INSTANCE);
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(ClearParticlePayload.TYPE, ClearParticlePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClearCachePayload.TYPE, ClearCachePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(NormalPayload.TYPE, NormalPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ConditionalPayload.TYPE, ConditionalPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ParameterPayload.TYPE, ParameterPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ImagePayload.TYPE, ImagePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ImageMatrixPayload.TYPE, ImageMatrixPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VideoPayload.TYPE, VideoPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VideoMatrixPayload.TYPE, VideoMatrixPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(GroupRemovePayload.TYPE, GroupRemovePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(GroupChangePayload.TYPE, GroupChangePayload.CODEC);
    }
}