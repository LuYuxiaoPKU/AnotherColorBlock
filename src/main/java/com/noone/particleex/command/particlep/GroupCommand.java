package com.noone.particleex.command.particlep;

import com.noone.particleex.command.argument.GroupChangeTypeArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.noone.particleex.network.payload.GroupChangePayload;
import com.noone.particleex.network.payload.GroupRemovePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class GroupCommand {
   public static void register(LiteralArgumentBuilder<ServerCommandSource> rootCommand) {
      rootCommand.then(CommandManager.literal("group")
          .then(CommandManager.literal("remove")
              .then(CommandManager.argument("group", StringArgumentType.string())
                  .executes(context -> removeExecute(context, StringArgumentType.getString(context, "group"), null, null))
                  .then(CommandManager.argument("expression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"\"age>100\""}))
                      .executes(context -> removeExecute(context, StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), null))
                      .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                          .executes(context -> removeExecute(context, StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), Vec3ArgumentType.getVec3(context, "pos")))
                      )
                  )
              )
          )
          .then(CommandManager.literal("change")
              .then(CommandManager.argument("type", GroupChangeTypeArgumentType.type())
                  .then(CommandManager.argument("group", StringArgumentType.string())
                      .then(CommandManager.argument("expression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"\"vy=0.1\""}))
                          .executes((context) -> changeExecute(context, GroupChangeTypeArgumentType.getType(context, "type"), StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), null, null))
                          .then(CommandManager.argument("conditionalExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"\"age>100\""}))
                              .executes(context -> changeExecute(context, GroupChangeTypeArgumentType.getType(context, "type"), StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), StringArgumentType.getString(context, "conditionalExpression"), null))
                              .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                  .executes(context -> changeExecute(context, GroupChangeTypeArgumentType.getType(context, "type"), StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), StringArgumentType.getString(context, "conditionalExpression"), Vec3ArgumentType.getVec3(context, "pos")))
                              )
                          )
                      )
                  )
              )
          )
      );
   }

   private static int removeExecute(CommandContext<ServerCommandSource> context, String group, String expression, Vec3d pos) {
       for (ServerPlayerEntity player : PlayerLookup.world(context.getSource().getWorld())) {
           ServerPlayNetworking.send(player, new GroupRemovePayload(group,expression,pos));
       }
       return 1;
   }

   private static int changeExecute(CommandContext<ServerCommandSource> context, int type, String group, String expression, String conditionalExpression, Vec3d pos) {
       for (ServerPlayerEntity player : PlayerLookup.world(context.getSource().getWorld())) {
           ServerPlayNetworking.send(player, new GroupChangePayload(type,group,expression,conditionalExpression,pos));
       }
        return 1;
    }
}
