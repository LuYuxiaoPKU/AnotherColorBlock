package com.noone.particleex.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.noone.particleex.command.particlep.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class ParticleExCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        LiteralArgumentBuilder<CommandSourceStack> rootCommand = Commands.literal("particleex").requires(source -> Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));
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
