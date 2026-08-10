package com.noone.particleex.command.particlep;

import com.noone.particleex.command.argument.Color4ArgumentType;
import com.noone.particleex.command.argument.Range3ArgumentType;
import com.noone.particleex.command.argument.Speed3ArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.noone.particleex.command.argument.SuggestDoubleArgumentType;
import com.noone.particleex.command.argument.SuggestIntegerArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.noone.particleex.network.payload.ConditionalPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class ConditionalCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand, CommandBuildContext registryAccess) {
      rootCommand.then(Commands.literal("conditional")
          .then(Commands.argument("name", ParticleArgument.particle(registryAccess))
              .then(Commands.argument("pos", Vec3Argument.vec3())
                  .then(Commands.argument("color", Color4ArgumentType.color4())
                      .then(Commands.argument("speed", Speed3ArgumentType.speed3())
                          .then(Commands.argument("range", Range3ArgumentType.range3())
                              .then(Commands.argument("expression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null", "\"y>0.25|y<-0.25\"", "\"dis>0.5&dis<1\"", "\"s1>0&s1<0.5&s2>0&dis<1\""}))
                                  .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"),Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), StringArgumentType.getString(context, "expression"), 0.1D, 0, null, 1.0D, null))
                                  .then(Commands.argument("step", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 0.1D))
                                      .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 0, null, 1.0D, null))
                                      .then(Commands.argument("age", SuggestIntegerArgumentType.integer(-1, Integer.MAX_VALUE, 0))
                                          .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null))
                                          .then(Commands.argument("speedExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null", "\"vy=0.1\"", "\"(vx,vy,vz)=((random(),random(),random())-0.5)*t/100\""}))
                                              .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null))
                                              .then(Commands.argument("speedStep", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 1.0D))
                                                  .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null))
                                                  .then(Commands.argument("group", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null"}))
                                                      .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")))
                                                  )
                                              )
                                          )
                                      )
                                  )
                              )
                          )
                      )
                  )
              )
          )
      );
   }

   private static int execute(CommandContext<CommandSourceStack> context, ParticleOptions effect, Vec3 pos, Vector4f color, Vec3 speed, Vec3 range, String expression, double step, int age, String speedExpression, double speedStep, String group) {
       for (ServerPlayer player : PlayerLookup.level(context.getSource().getLevel())) {
           ServerPlayNetworking.send(player, new ConditionalPayload(pos.x, pos.y, pos.z,color.x,color.y,color.z,color.w,speed.x,speed.y,speed.z, range.x, range.y, range.z, expression, step,age,speedExpression,speedStep,group,effect));
       }

      return 1;
   }
}
