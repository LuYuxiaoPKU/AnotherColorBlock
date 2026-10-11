package com.noone.particleex.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.noone.particleex.command.particlep.*;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class ParticleExCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> rootCommand = Commands.literal("particleex").requires((source) -> source.hasPermission(2));
        FunctionListCommand.register(rootCommand);
        ClearParticleCommand.register(rootCommand);
        ClearCacheCommand.register(rootCommand);
        NormalCommand.register(rootCommand,,dispatcher);
        ConditionalCommand.register(rootCommand,);
        ParameterCommand.register(rootCommand,);
        ImageCommand.register(rootCommand,);
        ImageMatrixCommand.register(rootCommand,);
        VideoCommand.register(rootCommand,);
        VideoMatrixCommand.register(rootCommand,);
        GroupCommand.register(rootCommand);
        dispatcher.register(rootCommand);
    }
}
