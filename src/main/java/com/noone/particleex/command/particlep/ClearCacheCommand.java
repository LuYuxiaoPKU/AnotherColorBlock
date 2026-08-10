package com.noone.particleex.command.particlep;

import com.noone.particleex.network.payload.ClearCachePayload;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClearCacheCommand {
   public static void register(LiteralArgumentBuilder<ServerCommandSource> rootCommand) {
      rootCommand.then(CommandManager.literal("clearcache").executes(ClearCacheCommand::execute));
   }

   public static int execute(CommandContext<ServerCommandSource> context) {

       for (ServerPlayerEntity player : PlayerLookup.world(context.getSource().getWorld())) {
           ServerPlayNetworking.send(player, new ClearCachePayload());
       }

      return 1;
   }

}
