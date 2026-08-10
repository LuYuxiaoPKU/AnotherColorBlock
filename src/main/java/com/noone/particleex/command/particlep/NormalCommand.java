package com.noone.particleex.command.particlep;

import com.mojang.brigadier.CommandDispatcher;
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
import com.noone.particleex.network.payload.NormalPayload;
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

public class NormalCommand {
   public static void register(LiteralArgumentBuilder<ServerCommandSource> rootCommand, CommandRegistryAccess registryAccess,CommandDispatcher<ServerCommandSource> dispatcher) {
      rootCommand.then(CommandManager.literal("normal")
          .then(CommandManager.argument("name", ParticleEffectArgumentType.particleEffect(registryAccess))
              .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                  .then(CommandManager.argument("color", Color4ArgumentType.color4())
                      .then(CommandManager.argument("speed", Speed3ArgumentType.speed3())
                          .then(CommandManager.argument("range", Range3ArgumentType.range3())
                              .then(CommandManager.argument("count", SuggestIntegerArgumentType.integer(0, Integer.MAX_VALUE, 1))
                                  .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), IntegerArgumentType.getInteger(context, "count"), 0, null, 1.0D, null))
                                  .then(CommandManager.argument("age", SuggestIntegerArgumentType.integer(-1, Integer.MAX_VALUE, 0))
                                      .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), IntegerArgumentType.getInteger(context, "count"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null))
                                      .then(CommandManager.argument("speedExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null", "\"vy=0.1\"", "\"(vx,vy,vz)=((random(),random(),random())-0.5)*t/100\""}))
                                          .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), IntegerArgumentType.getInteger(context, "count"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null))
                                          .then(CommandManager.argument("speedStep", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 1.0D))
                                              .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), IntegerArgumentType.getInteger(context, "count"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null))
                                              .then(CommandManager.argument("group", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null"}))
                                                  .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), Color4ArgumentType.getColor4(context, "color"), Speed3ArgumentType.getSpeed3(context, "speed"), Range3ArgumentType.getRange3(context, "range"), IntegerArgumentType.getInteger(context, "count"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")))
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

   private static int execute(CommandContext<ServerCommandSource> context, ParticleEffect effect, Vec3d pos, Vector4f color, Vec3d speed, Vec3d range, int count, int age, String expression, double step, String group) {
      for (ServerPlayerEntity player : PlayerLookup.world(context.getSource().getWorld())) {
          ServerPlayNetworking.send(player, new NormalPayload(pos.x,pos.y,pos.z,color.x,color.y,color.z,color.w,speed.x,speed.y,speed.z,range.x,range.y,range.z,count,age,expression,step,group,effect));
      }
      return 1;
   }
}
