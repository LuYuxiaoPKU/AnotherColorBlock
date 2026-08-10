package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
}
