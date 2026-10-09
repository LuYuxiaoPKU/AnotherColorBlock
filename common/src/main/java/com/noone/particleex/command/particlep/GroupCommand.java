package com.noone.particleex.command.particlep;

import com.noone.particleex.common.Bridge;
import com.noone.particleex.command.argument.GroupChangeTypeArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.noone.particleex.network.payload.GroupChangePayload;
import com.noone.particleex.network.payload.GroupRemovePayload;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;

public class GroupCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand) {
      rootCommand.then(Commands.literal("group")
          .then(Commands.literal("remove")
              .then(Commands.argument("group", StringArgumentType.string())
                  .executes(context -> removeExecute(context, StringArgumentType.getString(context, "group"), null, null))
                  .then(Commands.argument("expression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"\"age>100\""}))
                      .executes(context -> removeExecute(context, StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), null))
                      .then(Commands.argument("pos", Vec3Argument.vec3())
                          .executes(context -> removeExecute(context, StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), Vec3Argument.getVec3(context, "pos")))
                      )
                  )
              )
          )
          .then(Commands.literal("change")
              .then(Commands.argument("type", GroupChangeTypeArgumentType.type())
                  .then(Commands.argument("group", StringArgumentType.string())
                      .then(Commands.argument("expression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"\"vy=0.1\""}))
                          .executes((context) -> changeExecute(context, GroupChangeTypeArgumentType.getType(context, "type"), StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), null, null))
                          .then(Commands.argument("conditionalExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"\"age>100\""}))
                              .executes(context -> changeExecute(context, GroupChangeTypeArgumentType.getType(context, "type"), StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), StringArgumentType.getString(context, "conditionalExpression"), null))
                              .then(Commands.argument("pos", Vec3Argument.vec3())
                                  .executes(context -> changeExecute(context, GroupChangeTypeArgumentType.getType(context, "type"), StringArgumentType.getString(context, "group"), StringArgumentType.getString(context, "expression"), StringArgumentType.getString(context, "conditionalExpression"), Vec3Argument.getVec3(context, "pos")))
                              )
                          )
                      )
                  )
              )
          )
      );
   }

   private static int removeExecute(CommandContext<CommandSourceStack> context, String group, String expression, Vec3 pos) {
       Bridge.sendToPlayers(context.getSource().getLevel(), new GroupRemovePayload(group,expression,pos));
       return 1;
   }

   private static int changeExecute(CommandContext<CommandSourceStack> context, int type, String group, String expression, String conditionalExpression, Vec3 pos) {
       Bridge.sendToPlayers(context.getSource().getLevel(), new GroupChangePayload(type,group,expression,conditionalExpression,pos));
        return 1;
    }
}
