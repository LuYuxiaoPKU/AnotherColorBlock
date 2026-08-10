package com.noone.particleex.command.particlep;

import com.noone.particleex.network.payload.ClearCachePayload;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ClearCacheCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand) {
      rootCommand.then(Commands.literal("clearcache").executes(ClearCacheCommand::execute));
   }

   public static int execute(CommandContext<CommandSourceStack> context) {

       for (ServerPlayer player : PlayerLookup.level(context.getSource().getLevel())) {
           ServerPlayNetworking.send(player, new ClearCachePayload());
       }

      return 1;
   }

}
