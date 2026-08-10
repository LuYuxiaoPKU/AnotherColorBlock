package com.noone.particleex.command.particlep;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.noone.particleex.network.payload.ClearParticlePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClearParticleCommand {
   public static void register(LiteralArgumentBuilder<ServerCommandSource> rootCommand) {
      rootCommand.then(CommandManager.literal("clearparticle").executes(ClearParticleCommand::execute));
   }

   public static int execute(CommandContext<ServerCommandSource> context) {

       for (ServerPlayerEntity player : PlayerLookup.world(context.getSource().getWorld())) {
           ServerPlayNetworking.send(player, new ClearParticlePayload());
       }

      return 1;
   }
}
