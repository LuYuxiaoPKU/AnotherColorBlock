package com.noone.particleex.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.noone.particleex.command.particlep.*;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ParticleExCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        LiteralArgumentBuilder<ServerCommandSource> rootCommand = CommandManager.literal("particleex").requires(source -> source.hasPermissionLevel(2));
        FunctionListCommand.register(rootCommand);
        ClearParticleCommand.register(rootCommand);
        ClearCacheCommand.register(rootCommand);
        NormalCommand.register(rootCommand,registryAccess,dispatcher);
        ConditionalCommand.register(rootCommand,registryAccess);
        ParameterCommand.register(rootCommand,registryAccess);
        ImageCommand.register(rootCommand,registryAccess);
        ImageMatrixCommand.register(rootCommand,registryAccess);
        VideoCommand.register(rootCommand,registryAccess);
        VideoMatrixCommand.register(rootCommand,registryAccess);
        GroupCommand.register(rootCommand);
        dispatcher.register(rootCommand);
    }
}
