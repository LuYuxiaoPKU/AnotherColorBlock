package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.*;
import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.payload.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
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
        // 平台桥：命令层发送 → Fabric 网络发送
        Bridge.setSender((world, payload) -> PlayerLookup.world(world).forEach(player -> ServerPlayNetworking.send(player, payload)));
        registerPayloads();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> ParticleExCommand.register(dispatcher, registryAccess));
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "color4"), Color4ArgumentType.class, ConstantArgumentSerializer.of(Color4ArgumentType::color4));
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "flip"), FlipArgumentType.class, ConstantArgumentSerializer.of(FlipArgumentType::flip));
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "group_change"), GroupChangeTypeArgumentType.class, ConstantArgumentSerializer.of(GroupChangeTypeArgumentType::type));
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "range3"), Range3ArgumentType.class, ConstantArgumentSerializer.of(Range3ArgumentType::range3));
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "rotate"), RotateArgumentType.class, ConstantArgumentSerializer.of(RotateArgumentType::rotate));
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "speed3"), Speed3ArgumentType.class, ConstantArgumentSerializer.of(Speed3ArgumentType::speed3));
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "suggest_string"), SuggestArgumentType.class, SuggestArgumentType.Serializer.INSTANCE);
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "suggest_double"), SuggestDoubleArgumentType.class, SuggestDoubleArgumentType.Serializer.INSTANCE);
        ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "suggest_integer"), SuggestIntegerArgumentType.class, SuggestIntegerArgumentType.Serializer.INSTANCE);
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
