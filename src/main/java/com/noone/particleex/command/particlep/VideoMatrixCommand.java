package com.noone.particleex.command.particlep;

import com.noone.particleex.command.argument.Speed3ArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.noone.particleex.command.argument.SuggestDoubleArgumentType;
import com.noone.particleex.command.argument.SuggestIntegerArgumentType;
import com.noone.particleex.network.payload.VideoMatrixPayload;
import com.noone.particleex.util.MatrixUtil;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
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

public class VideoMatrixCommand {
   public static void register(LiteralArgumentBuilder<ServerCommandSource> rootCommand, CommandRegistryAccess registryAccess) {
      rootCommand.then(CommandManager.literal("videomatrix")
          .then(CommandManager.argument("name", ParticleEffectArgumentType.particleEffect(registryAccess))
              .then(CommandManager.argument("pos", Vec3ArgumentType.vec3())
                  .then(CommandManager.argument("path", StringArgumentType.string())
                      .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), 0.1D, "E3", 10.0D, null, 0, null, 1.0D, null))
                      .then(CommandManager.argument("scaling", SuggestDoubleArgumentType.doubleArg(0.0D, Double.MAX_VALUE, 0.1D))
                          .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), "E3", 10.0D, null, 0, null, 1.0D, null))
                          .then(CommandManager.argument("matrix", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"E3", "E4", "\"(1,0,0,0,,0,1,0,0,,0,0,1,-100,,0,0,0,1)\"", "\"(0.5,-0.5,0.7071,-0.7071,,0.1464466,0.8535534,0.5,-5,,-0.8535534,-0.1464466,0.5,-5,,0,0,0,1)\""}))
                              .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), null, 0, null, 1.0D, null))
                              .then(CommandManager.argument("dpb", SuggestDoubleArgumentType.doubleArg(0.0D, Double.MAX_VALUE, 10.0D))
                                  .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), null, 0, null, 1.0D, null))
                                  .then(CommandManager.argument("speed", Speed3ArgumentType.speed3())
                                      .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), 0, null, 1.0D, null))
                                      .then(CommandManager.argument("age", SuggestIntegerArgumentType.integer(-1, Integer.MAX_VALUE, 0))
                                          .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null))
                                          .then(CommandManager.argument("speedExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null", "\"vy=0.1\"", "\"(vx,vy,vz)=((random(),random(),random())-0.5)*t/100\""}))
                                              .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null))
                                              .then(CommandManager.argument("speedStep", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 1.0D))
                                                  .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null))
                                                  .then(CommandManager.argument("group", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null"}))
                                                      .executes(context -> execute(context, ParticleEffectArgumentType.getParticle(context, "name"), Vec3ArgumentType.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")))
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

   private static int execute(CommandContext<ServerCommandSource> context, ParticleEffect effect, Vec3d pos, String path, double scaling, String matrixStr, double dpb, Vec3d speed, int age, String speedExpression, double speedStep, String group) {
       for (ServerPlayerEntity player : PlayerLookup.world(context.getSource().getWorld())) {
           ServerPlayNetworking.send(player, new VideoMatrixPayload(pos.x,pos.y,pos.z,path,scaling,MatrixUtil.toMat(matrixStr),dpb,speed,age,speedExpression,speedStep,group,effect));
       }
        return 1;
    }
}
