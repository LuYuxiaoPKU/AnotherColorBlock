package com.noone.particleex.command.particlep;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.noone.particleex.network.payload.ClearParticlePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ClearParticleCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand) {
      rootCommand.then(Commands.literal("clearparticle").executes(ClearParticleCommand::execute));
   }

   public static int execute(CommandContext<CommandSourceStack> context) {

       for (ServerPlayer player : PlayerLookup.level(context.getSource().getLevel())) {
           ServerPlayNetworking.send(player, new ClearParticlePayload());
       }

      return 1;
   }
}
