package com.noone.particleex.command.particlep;

import com.noone.particleex.common.Bridge;
import com.noone.particleex.command.argument.FlipArgumentType;
import com.noone.particleex.command.argument.RotateArgumentType;
import com.noone.particleex.command.argument.Speed3ArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.noone.particleex.command.argument.SuggestDoubleArgumentType;
import com.noone.particleex.command.argument.SuggestIntegerArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.noone.particleex.network.payload.VideoPayload;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;

public class VideoCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand, CommandBuildContext registryAccess) {
      rootCommand.then(Commands.literal("video")
          .then(Commands.argument("name", ParticleArgument.particle(registryAccess))
              .then(Commands.argument("pos", Vec3Argument.vec3())
                  .then(Commands.argument("path", StringArgumentType.string())
                      .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), 0.1D, 0, 0, 0, 0, 10.0D, null, 0, null, 1.0D, null))
                      .then(Commands.argument("scaling", SuggestDoubleArgumentType.doubleArg(0.0D, Double.MAX_VALUE, 0.1D))
                          .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), 0, 0, 0, 0, 10.0D, null, 0, null, 1.0D, null))
                          .then(Commands.argument("xRotate", RotateArgumentType.rotate())
                              .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), 0, 0, 0, 10.0D, null, 0, null, 1.0D, null))
                              .then(Commands.argument("yRotate", RotateArgumentType.rotate())
                                  .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), 0, 0, 10.0D, null, 0, null, 1.0D, null))
                                  .then(Commands.argument("zRotate", RotateArgumentType.rotate())
                                      .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), RotateArgumentType.getRotate(context, "zRotate"), 0, 10.0D, null, 0, null, 1.0D, null))
                                      .then(Commands.argument("flip", FlipArgumentType.flip())
                                          .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), RotateArgumentType.getRotate(context, "zRotate"), FlipArgumentType.getFlip(context, "flip"), 10.0D, null, 0, null, 1.0D, null))
                                          .then(Commands.argument("dpb", SuggestDoubleArgumentType.doubleArg(0.0D, Double.MAX_VALUE, 10.0D))
                                              .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), RotateArgumentType.getRotate(context, "zRotate"), FlipArgumentType.getFlip(context, "flip"), DoubleArgumentType.getDouble(context, "dpb"), null, 0, null, 1.0D, null))
                                              .then(Commands.argument("speed", Speed3ArgumentType.speed3())
                                                  .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), RotateArgumentType.getRotate(context, "zRotate"), FlipArgumentType.getFlip(context, "flip"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), 0, null, 1.0D, null))
                                                  .then(Commands.argument("age", SuggestIntegerArgumentType.integer(-1, Integer.MAX_VALUE, 0))
                                                      .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), RotateArgumentType.getRotate(context, "zRotate"), FlipArgumentType.getFlip(context, "flip"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null))
                                                      .then(Commands.argument("speedExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null", "\"vy=0.1\"", "\"(vx,vy,vz)=((random(),random(),random())-0.5)*t/100\""}))
                                                          .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), RotateArgumentType.getRotate(context, "zRotate"), FlipArgumentType.getFlip(context, "flip"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null))
                                                          .then(Commands.argument("speedStep", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 1.0D))
                                                              .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), RotateArgumentType.getRotate(context, "zRotate"), FlipArgumentType.getFlip(context, "flip"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null))
                                                              .then(Commands.argument("group", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null"}))
                                                                  .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), RotateArgumentType.getRotate(context, "xRotate"), RotateArgumentType.getRotate(context, "yRotate"), RotateArgumentType.getRotate(context, "zRotate"), FlipArgumentType.getFlip(context, "flip"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")))
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
                  )
              )
          )
      );
   }

   private static int execute(CommandContext<CommandSourceStack> context, ParticleOptions effect, Vec3 pos, String path, double scaling, int xRotate, int yRotate, int zRotate, int flip, double dpb, Vec3 speed, int age, String speedExpression, double speedStep, String group) {
       if (flip == 2) {
           zRotate += 2;
       }
       Bridge.sendToPlayers(context.getSource().getLevel(), new VideoPayload(pos.x,pos.y,pos.z,path,scaling,xRotate,yRotate,zRotate,flip,dpb,speed,age,speedExpression,speedStep,group,effect));
       return 1;
    }
}
