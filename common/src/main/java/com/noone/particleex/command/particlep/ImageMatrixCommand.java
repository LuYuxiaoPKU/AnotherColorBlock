package com.noone.particleex.command.particlep;

import com.noone.particleex.common.Bridge;
import com.noone.particleex.command.argument.Speed3ArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.noone.particleex.command.argument.SuggestDoubleArgumentType;
import com.noone.particleex.command.argument.SuggestIntegerArgumentType;
import com.noone.particleex.network.payload.ImageMatrixPayload;
import com.noone.particleex.util.MatrixUtil;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;

public class ImageMatrixCommand {
   public static void register(LiteralArgumentBuilder<CommandSourceStack> rootCommand, CommandBuildContext registryAccess) {
      rootCommand.then(Commands.literal("imagematrix")
          .then(Commands.argument("name", ParticleArgument.particle(registryAccess))
              .then(Commands.argument("pos", Vec3Argument.vec3())
                  .then(Commands.argument("path", StringArgumentType.string())
                      .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), 0.1D, "E3", 10.0D, null, 0, null, 1.0D, null))
                      .then(Commands.argument("scaling", SuggestDoubleArgumentType.doubleArg(0.0D, Double.MAX_VALUE, 0.1D))
                          .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), "E3", 10.0D, null, 0, null, 1.0D, null))
                          .then(Commands.argument("matrix", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"E3", "E4", "\"(1,0,0,0,,0,1,0,0,,0,0,1,-100,,0,0,0,1)\"", "\"(0.5,-0.5,0.7071,-0.7071,,0.1464466,0.8535534,0.5,-5,,-0.8535534,-0.1464466,0.5,-5,,0,0,0,1)\""}))
                              .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), null, 0, null, 1.0D, null))
                              .then(Commands.argument("dpb", SuggestDoubleArgumentType.doubleArg(0.0D, Double.MAX_VALUE, 10.0D))
                                  .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), null, 0, null, 1.0D, null))
                                  .then(Commands.argument("speed", Speed3ArgumentType.speed3())
                                      .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), 0, null, 1.0D, null))
                                      .then(Commands.argument("age", SuggestIntegerArgumentType.integer(-1, Integer.MAX_VALUE, 0))
                                          .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), null, 1.0D, null))
                                          .then(Commands.argument("speedExpression", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null", "\"vy=0.1\"", "\"(vx,vy,vz)=((random(),random(),random())-0.5)*t/100\""}))
                                              .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), 1.0D, null))
                                              .then(Commands.argument("speedStep", SuggestDoubleArgumentType.doubleArg(Math.ulp(0.0F), Double.MAX_VALUE, 1.0D))
                                                  .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), null))
                                                  .then(Commands.argument("group", SuggestArgumentType.argument(StringArgumentType.string(), new String[]{"null"}))
                                                      .executes(context -> execute(context, ParticleArgument.getParticle(context, "name"), Vec3Argument.getVec3(context, "pos"), StringArgumentType.getString(context, "path"), DoubleArgumentType.getDouble(context, "scaling"), StringArgumentType.getString(context, "matrix"), DoubleArgumentType.getDouble(context, "dpb"), Speed3ArgumentType.getSpeed3(context, "speed"), IntegerArgumentType.getInteger(context, "age"), StringArgumentType.getString(context, "speedExpression"), DoubleArgumentType.getDouble(context, "speedStep"), StringArgumentType.getString(context, "group")))
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

   private static int execute(CommandContext<CommandSourceStack> context, ParticleOptions effect, Vec3 pos, String path, double scaling, String matrixStr, double dpb, Vec3 speed, int age, String speedExpression, double speedStep, String group) {
       Bridge.sendToPlayers(context.getSource().getLevel(), new ImageMatrixPayload(pos.x,pos.y,pos.z,path,scaling,MatrixUtil.toMat(matrixStr),dpb,speed,age,speedExpression,speedStep,group,effect));
        return 1;
    }
}
