package com.noone.particleex.command.particlep;

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
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public class ParameterCommand {
   public static void register(LiteralArgumentBuilder<ServerCommandSource> rootCommand,CommandRegistryAccess registryAccess) {
      rootCommand
          .then(CommandManager.literal("parameter")
              .then(
                  getStart(registryAccess,
                      getColoe(
                          getMid(
                              new String[]{"null", "\"x,y=t,sin(t)\"", "\"x=t;y=t^2\""},
                              context -> execute(context, false, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                              context -> execute(context, false, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                              getEnd(
                                  context -> execute(context, false, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                  context -> execute(context, false, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                  context -> execute(context, false, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                  context -> execute(context, false, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                              )
                          )
                      )
                  )
              )
          )
          .then(CommandManager.literal("polarparameter")
              .then(
                  getStart(registryAccess,
                      getColoe(
                          getMid(
                              new String[]{"null", "\"s1,s2,dis=t*10,t*PI/20,1\""},
                              context -> execute(context, true, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                              context -> execute(context, true, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                              getEnd(
                                  context -> execute(context, true, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                  context -> execute(context, true, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                  context -> execute(context, true, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                  context -> execute(context, true, false, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                              )
                          )
                      )
                  )
              )
          )
          .then(CommandManager.literal("tickparameter")
              .then(
                  getStart(registryAccess,
                      getColoe(
                          getMid(
                              new String[]{"null", "\"x,y=t,sin(t)\"", "\"x=t;y=t^2\""},
                              context -> execute(context, false, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                              context -> execute(context, false, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                              getCpt(
                                  context -> execute(context, false, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), 0, null, 1.0D, null),
                                  getEnd(
                                      context -> execute(context, false, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                      context -> execute(context, false, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                      context -> execute(context, false, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                      context -> execute(context, false, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                                  )
                              )
                          )
                      )
                  )
              )
          )
          .then(CommandManager.literal("tickpolarparameter")
              .then(
                  getStart(registryAccess,
                      getColoe(
                          getMid(
                              new String[]{"null", "\"s1,s2,dis=t*10,t*PI/20,1\""},
                              context -> execute(context, true, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                              context -> execute(context, true, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                              getCpt(
                                  context -> execute(context, true, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), 0, null, 1.0D, null),
                                  getEnd(
                                      context -> execute(context, true, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                      context -> execute(context, true, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                      context -> execute(context, true, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                      context -> execute(context, true, true, false, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                                  )
                              )
                          )
                      )
                  )
              )
          )
          .then(CommandManager.literal("rgbaparameter").
              then(
                  getStart(registryAccess,
                      getMid(
                          new String[]{"null", "\"x,y,cr,cg,cb=t,sin(t),sin(t/7)/4+0.75,sin(t/5)/4+0.75,sin(t/3)/4+0.75\""},
                          context -> execute(context, false, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                          context -> execute(context, false, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                          getEnd(
                              context -> execute(context, false, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                              context -> execute(context, false, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                              context -> execute(context, false, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                              context -> execute(context, false, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")
                              )
                          )
                      )
                  )
              )
          )
          .then(CommandManager.literal("rgbapolarparameter")
              .then(
                  getStart(registryAccess,
                      getMid(
                          new String[]{"null", "\"s1,s2,dis=t*10,t*PI/20,1;cr,cg,cb=sin(t/7)/4+0.75,sin(t/5)/4+0.75,sin(t/3)/4+0.75\""},
                          context -> execute(context, true, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                          context -> execute(context, true, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                          getEnd(
                              context -> execute(context, true, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                              context -> execute(context, true, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                              context -> execute(context, true, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                              context -> execute(context, true, false, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                          )
                      )
                  )
              )
          )
          .then(CommandManager.literal("rgbatickparameter")
              .then(
                  getStart(registryAccess,
                      getMid(
                          new String[]{"null", "\"x,y,cr,cg,cb=t,sin(t),sin(t/7)/4+0.75,sin(t/5)/4+0.75,sin(t/3)/4+0.75\""},
                          context -> execute(context, false, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                          context -> execute(context, false, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                          getCpt(
                              context -> execute(context, false, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), 0, null, 1.0D, null),
                              getEnd(
                                  context -> execute(context, false, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                  context -> execute(context, false, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                  context -> execute(context, false, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                  context -> execute(context, false, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")
                                  )
                              )
                          )
                      )
                  )
              )
          )
          .then(CommandManager.literal("rgbatickpolarparameter")
              .then(
                  getStart(registryAccess,
                      getMid(
                          new String[]{"null", "\"s1,s2,dis=t*10,t*PI/20,1;cr,cg,cb=sin(t/7)/4+0.75,sin(t/5)/4+0.75,sin(t/3)/4+0.75\""},
                          context -> execute(context, true, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), 0.1D, 10, 0, null, 1.0D, null),
                          context -> execute(context, true, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), 10, 0, null, 1.0D, null),
                          getCpt(
                              context -> execute(context, true, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), 0, null, 1.0D, null),
                              getEnd(
                                  context -> execute(context, true, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null),
                                  context -> execute(context, true, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null),
                                  context -> execute(context, true, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null),
                                  context -> execute(context, true, true, true, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), null, Speed3ArgumentType.getSpeed3(context, "speed"), DoubleArgumentType.getDouble(context, "begin"), DoubleArgumentType.getDouble(context, "end"), StringArgumentType.getString(context, "expression"), DoubleArgumentType.getDouble(context, "step"), IntegerArgumentType.getInteger(context, "cpt"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group"))
                              )
                          )
                      )
                  )
              )
          );
   }

   private static ArgumentBuilder<ServerCommandSource, ?> getStart(CommandRegistryAccess registryAccess,ArgumentBuilder<ServerCommandSource, ?> then) {
      return CommandManager.argument("name", ParticleEffectArgumentType.particleEffect(registryAccess))
              .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                  .then(then)
              );
   }

   private static ArgumentBuilder<ServerCommandSource, ?> getColoe(ArgumentBuilder<ServerCommandSource, ?> then) {
      return CommandManager.argument("color", Color4ArgumentType.color4())
              .then(then);
   }

   private static ArgumentBuilder<ServerCommandSource, ?> getMid(String[] suggests, Command<ServerCommandSource> command1, Command<ServerCommandSource> command2, ArgumentBuilder<ServerCommandSource, ?> then) {
      return CommandManager.argument("speed", Speed3ArgumentType.speed3())
              .then(CommandManager.argument("begin", SuggestDoubleArgumentType.doubleArg(-1.7976931348623157E308D, Double.MAX_VALUE, -10.0D))
                  .then(CommandManager.argument("end", SuggestDoubleArgumentType.doubleArg(-1.7976931348623157E308D, Double.MAX_VALUE, 10.0D))
                      .then(CommandManager.argument("expression", SuggestArgumentType.argument(StringArgumentType.string(), suggests))
                          .executes(command1)
                          .then(CommandManager.argument("step", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 0.1D))
                              .executes(command2)
                              .then(then)
                          )
                      )
                  )
              );
   }

   private static ArgumentBuilder<ServerCommandSource, ?> getCpt(Command<ServerCommandSource> command, ArgumentBuilder<ServerCommandSource, ?> then) {
      return CommandManager.argument("cpt", SuggestIntegerArgumentType.integer(1, Integer.MAX_VALUE, 10))
              .executes(command)
              .then(then);
   }

   private static ArgumentBuilder<ServerCommandSource, ?> getEnd(Command<ServerCommandSource> command1, Command<ServerCommandSource> command2, Command<ServerCommandSource> command3, Command<ServerCommandSource> command4) {
      return CommandManager.argument("age", SuggestIntegerArgumentType.integer(-1, Integer.MAX_VALUE, 0))
              .executes(command1)
              .then(CommandManager.argument("speedExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null", "\"vy=0.1\"", "\"(vx,vy,vz)=((random(),random(),random())-0.5)*t/100\""}))
                  .executes(command2)
                  .then((CommandManager.argument("speedStep", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 1.0D))
                      .executes(command3))
                      .then(CommandManager.argument("group", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null"}))
                          .executes(command4)
                      )
                  )
              );
   }

   private static int execute(CommandContext<ServerCommandSource> context, boolean polar, boolean tick, boolean rgba, ParticleEffect effect, Vec3d pos, Vector4f color, Vec3d speed, double begin, double end, String expression, double step, int cpt, int age, String speedExpression, double speedStep, String group) {
       for (ServerPlayerEntity player : PlayerLookup.world(context.getSource().getWorld())) {
           ServerPlayNetworking.send(player, new ParameterPayload(polar,tick,rgba,pos.x,pos.y,pos.z,color,speed.x,speed.y,speed.z,begin,end,expression,step,cpt,age,speedExpression,speedStep,group,effect));
       }
        return 1;
    }
}
