package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.*;
import com.noone.particleex.network.payload.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
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
        PayloadTypeRegistry.playS2C().register(ClearParticlePayload.ID, ClearParticlePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClearCachePayload.ID, ClearCachePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(NormalPayload.ID, NormalPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConditionalPayload.ID, ConditionalPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ParameterPayload.ID, ParameterPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ImagePayload.ID, ImagePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ImageMatrixPayload.ID, ImageMatrixPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(VideoPayload.ID, VideoPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(VideoMatrixPayload.ID, VideoMatrixPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GroupRemovePayload.ID, GroupRemovePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GroupChangePayload.ID, GroupChangePayload.CODEC);
    }
}
