package com.noone.particleex.command.particlep;

import com.noone.particleex.common.Bridge;
import com.noone.particleex.command.argument.Color4ArgumentType;
import com.noone.particleex.command.argument.Speed3ArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.noone.particleex.command.argument.SuggestDoubleArgumentType;
import com.noone.particleex.command.argument.SuggestIntegerArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.noone.particleex.network.payload.ParameterPayload;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.Vec3Argument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class ParameterCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand,CommandBuildContext registryAccess) {
      rootCommand
          .then(Commands.literal("parameter")
              .then(
                  getStart(registryAccess,
                      getColoe(
                          getMid(
                              new String[]{"null", "\"x,y=t,sin(t)\"", "\"x=t;y=t^2\""},
                              context -> execute(context, false, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                              context -> execute(context, false, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                              getEnd(
                                  context -> execute(context, false, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                  context -> execute(context, false, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                  context -> execute(context, false, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                  context -> execute(context, false, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                              )
                          )
                      )
                  )
              )
          )
          .then(Commands.literal("polarparameter")
              .then(
                  getStart(registryAccess,
                      getColoe(
                          getMid(
                              new String[]{"null", "\"s1,s2,dis=t*10,t*PI/20,1\""},
                              context -> execute(context, true, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                              context -> execute(context, true, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                              getEnd(
                                  context -> execute(context, true, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                  context -> execute(context, true, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                  context -> execute(context, true, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                  context -> execute(context, true, false, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                              )
                          )
                      )
                  )
              )
          )
          .then(Commands.literal("tickparameter")
              .then(
                  getStart(registryAccess,
                      getColoe(
                          getMid(
                              new String[]{"null", "\"x,y=t,sin(t)\"", "\"x=t;y=t^2\""},
                              context -> execute(context, false, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                              context -> execute(context, false, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                              getCpt(
                                  context -> execute(context, false, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), 0, null, 1.0D, null),
                                  getEnd(
                                      context -> execute(context, false, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                      context -> execute(context, false, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                      context -> execute(context, false, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                      context -> execute(context, false, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                                  )
                              )
                          )
                      )
                  )
              )
          )
          .then(Commands.literal("tickpolarparameter")
              .then(
                  getStart(registryAccess,
                      getColoe(
                          getMid(
                              new String[]{"null", "\"s1,s2,dis=t*10,t*PI/20,1\""},
                              context -> execute(context, true, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                              context -> execute(context, true, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                              getCpt(
                                  context -> execute(context, true, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), 0, null, 1.0D, null),
                                  getEnd(
                                      context -> execute(context, true, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                      context -> execute(context, true, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                      context -> execute(context, true, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                      context -> execute(context, true, true, false, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                                  )
                              )
                          )
                      )
                  )
              )
          )
          .then(Commands.literal("rgbaparameter").
              then(
                  getStart(registryAccess,
                      getMid(
                          new String[]{"null", "\"x,y,cr,cg,cb=t,sin(t),sin(t/7)/4+0.75,sin(t/5)/4+0.75,sin(t/3)/4+0.75\""},
                          context -> execute(context, false, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                          context -> execute(context, false, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                          getEnd(
                              context -> execute(context, false, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                              context -> execute(context, false, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                              context -> execute(context, false, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                              context -> execute(context, false, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")
                              )
                          )
                      )
                  )
              )
          )
          .then(Commands.literal("rgbapolarparameter")
              .then(
                  getStart(registryAccess,
                      getMid(
                          new String[]{"null", "\"s1,s2,dis=t*10,t*PI/20,1;cr,cg,cb=sin(t/7)/4+0.75,sin(t/5)/4+0.75,sin(t/3)/4+0.75\""},
                          context -> execute(context, true, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                          context -> execute(context, true, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                          getEnd(
                              context -> execute(context, true, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                              context -> execute(context, true, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                              context -> execute(context, true, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                              context -> execute(context, true, false, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                          )
                      )
                  )
              )
          )
          .then(Commands.literal("rgbatickparameter")
              .then(
                  getStart(registryAccess,
                      getMid(
                          new String[]{"null", "\"x,y,cr,cg,cb=t,sin(t),sin(t/7)/4+0.75,sin(t/5)/4+0.75,sin(t/3)/4+0.75\""},
                          context -> execute(context, false, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                          context -> execute(context, false, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                          getCpt(
                              context -> execute(context, false, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), 0, null, 1.0D, null),
                              getEnd(
                                  context -> execute(context, false, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                  context -> execute(context, false, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                  context -> execute(context, false, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                  context -> execute(context, false, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")
                                  )
                              )
                          )
                      )
                  )
              )
          )
          .then(Commands.literal("rgbatickpolarparameter")
              .then(
                  getStart(registryAccess,
                      getMid(
                          new String[]{"null", "\"s1,s2,dis=t*10,t*PI/20,1;cr,cg,cb=sin(t/7)/4+0.75,sin(t/5)/4+0.75,sin(t/3)/4+0.75\""},
                          context -> execute(context, true, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                          context -> execute(context, true, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                          getCpt(
                              context -> execute(context, true, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), 0, null, 1.0D, null),
                              getEnd(
                                  context -> execute(context, true, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                  context -> execute(context, true, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                  context -> execute(context, true, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                  context -> execute(context, true, true, true, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                              )
                          )
                      )
                  )
              )
          );
   }

   private static ArgumentBuilder<CommandSourceStack, ?> getStart(CommandBuildContext registryAccess,ArgumentBuilder<CommandSourceStack, ?> then) {
      return Commands.argument("name", ParticleArgument.particleEffect(registryAccess))
              .then(Commands.argument("pos", Vec3Argument.vec3())
                  .then(then)
              );
   }

   private static ArgumentBuilder<CommandSourceStack, ?> getColoe(ArgumentBuilder<CommandSourceStack, ?> then) {
      return Commands.argument("color", Color4ArgumentType.color4())
              .then(then);
   }

   private static ArgumentBuilder<CommandSourceStack, ?> getMid(String[] suggests, Command<CommandSourceStack> command1, Command<CommandSourceStack> command2, ArgumentBuilder<CommandSourceStack, ?> then) {
      return Commands.argument("speed", Speed3ArgumentType.speed3())
              .then(Commands.argument("begin", SuggestDoubleArgumentType.doubleArg(-1.7976931348623157E308D, Double.MAX_VALUE, -10.0D))
                  .then(Commands.argument("end", SuggestDoubleArgumentType.doubleArg(-1.7976931348623157E308D, Double.MAX_VALUE, 10.0D))
                      .then(Commands.argument("expression", SuggestArgumentType.argument(StringArgumentType.string(), suggests))
                          .executes(command1)
                          .then(Commands.argument("step", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 0.1D))
                              .executes(command2)
                              .then(then)
                          )
                      )
                  )
              );
   }

   private static ArgumentBuilder<CommandSourceStack, ?> getCpt(Command<CommandSourceStack> command, ArgumentBuilder<CommandSourceStack, ?> then) {
      return Commands.argument("cpt", SuggestIntegerArgumentType.integer(1, Integer.MAX_VALUE, 10))
              .executes(command)
              .then(then);
   }

   private static ArgumentBuilder<CommandSourceStack, ?> getEnd(Command<CommandSourceStack> command1, Command<CommandSourceStack> command2, Command<CommandSourceStack> command3, Command<CommandSourceStack> command4) {
      return Commands.argument("age", SuggestIntegerArgumentType.integer(-1, Integer.MAX_VALUE, 0))
              .executes(command1)
              .then(Commands.argument("speedExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null", "\"vy=0.1\"", "\"(vx,vy,vz)=((random(),random(),random())-0.5)*t/100\""}))
                  .executes(command2)
                  .then((Commands.argument("speedStep", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 1.0D))
                      .executes(command3))
                      .then(Commands.argument("group", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null"}))
                          .executes(command4)
                      )
                  )
              );
   }

   private static int execute(CommandContext<CommandSourceStack> context, boolean polar, boolean tick, boolean rgba, ParticleOptions effect, Vec3 pos, Vector4f color, Vec3 speed, double begin, double end, String expression, double step, int cpt, int age, String speedExpression, double speedStep, String group) {
       Bridge.sendToPlayers(context.getSource().getLevel(), new ParameterPayload(polar,tick,rgba,pos.x,pos.y,pos.z,color,speed.x,speed.y,speed.z,begin,end,expression,step,cpt,age,speedExpression,speedStep,group,effect));
        return 1;
    }
}
