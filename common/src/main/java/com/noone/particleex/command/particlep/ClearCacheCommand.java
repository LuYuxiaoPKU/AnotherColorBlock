package com.noone.particleex.command.particlep;

import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.payload.ClearCachePayload;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class ClearCacheCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand) {
      rootCommand.then(Commands.literal("clearcache").executes(ClearCacheCommand::execute));
   }

   public static int execute(CommandContext<CommandSourceStack> context) {

       Bridge.sendToPlayers(context.getSource().getLevel(), new ClearCachePayload());

      return 1;
   }

}
