package com.noone.particleex.command.particlep;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.payload.ClearParticlePayload;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class ClearParticleCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand) {
      rootCommand.then(Commands.literal("clearparticle").executes(ClearParticleCommand::execute));
   }

   public static int execute(CommandContext<CommandSourceStack> context) {

       Bridge.sendToPlayers(context.getSource().getLevel(), new ClearParticlePayload());

      return 1;
   }
}
