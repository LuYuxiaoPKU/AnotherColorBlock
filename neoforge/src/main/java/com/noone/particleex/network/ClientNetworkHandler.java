package com.noone.particleex.network;

import com.noone.particleex.mixin.IBillboardParticleMixin;
import com.noone.particleex.mixin.IParticleMixin;
import com.noone.particleex.network.payload.*;
import com.noone.particleex.util.ExpressionUtil;
import com.noone.particleex.util.GroupUtil;
import com.noone.particleex.util.IExecutable;
import com.noone.particleex.util.IParticle;
import com.noone.particleex.util.ImageUtil;
import com.noone.particleex.util.ParticleStruct;
import com.noone.particleex.util.ParticleUtil;
import java.util.Iterator;
import java.util.Random;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class ClientNetworkHandler {
   private static final Random RANDOM = new Random();

   public static void clearParticle(IPayloadContext context) {
      Minecraft client = Minecraft.getInstance();
      client.execute(() -> {
         GroupUtil.clear();
         client.particleEngine.setLevel(client.level);
      });
   }

   public static void clearCache(IPayloadContext context) {
      Minecraft.getInstance().execute(ImageUtil::clear);
   }

   public static void normal(NormalPayload payload, IPayloadContext context) {
      double x = payload.x();
      double y = payload.y();
      double z = payload.z();
      float red = payload.red();
      float green = payload.green();
      float blue = payload.blue();
      float alpha = payload.alpha();
      double vx = payload.vx();
      double vy = payload.vy();
      double vz = payload.vz();
      double dx = payload.dx();
      double dy = payload.dy();
      double dz = payload.dz();
      int count = payload.count();
      int age = payload.age();
      String speedExpression = payload.speedExpression();
      double speedStep = payload.speedStep();
      String group = payload.group();
      ParticleOptions effect = payload.effect();
      Minecraft.getInstance().execute(() -> {
         for(int i = 0; i < count; ++i) {
            double rx = RANDOM.nextGaussian() * dx;
            double ry = RANDOM.nextGaussian() * dy;
            double rz = RANDOM.nextGaussian() * dz;
            ParticleUtil.spawnParticle(effect, x + rx, y + ry, z + rz, x, y, z, red, green, blue, alpha, vx, vy, vz, age, speedExpression, speedStep, group);
         }

      });
   }

   public static void conditional(ConditionalPayload payload, IPayloadContext context) {
      double x = payload.x();
      double y = payload.y();
      double z = payload.z();
      float red = payload.red();
      float green = payload.green();
      float blue = payload.blue();
      float alpha = payload.alpha();
      double vx = payload.vx();
      double vy = payload.vy();
      double vz = payload.vz();
      double dx = payload.dx();
      double dy = payload.dy();
      double dz = payload.dz();
      String expression = payload.expression();
      double step = payload.step();
      int age = payload.age();
      String speedExpression = payload.speedExpression();
      double speedStep = payload.speedStep();
      String group = payload.group();
      ParticleOptions effect = payload.effect();
      Minecraft.getInstance().execute(() -> {
         IExecutable exe = ExpressionUtil.parse(expression);
         for(double cx = -dx; cx <= dx; cx += step) {
            for(double cy = -dy; cy <= dy; cy += step) {
               for(double cz = -dz; cz <= dz; cz += step) {
                  if (exe != null) {
                     ParticleStruct data = exe.getData();
                     data.x = cx;
                     data.y = cy;
                     data.z = cz;
                     data.s1 = Math.atan2(cz, cx);
                     data.s2 = Math.atan2(cy, Math.hypot(cx, cz));
                     data.dis = Math.sqrt(cx * cx + cy * cy + cz * cz);
                     if (exe.invoke() == 0) {
                        continue;
                     }
                  }
                  ParticleUtil.spawnParticle(effect, x + cx, y + cy, z + cz, x, y, z, red, green, blue, alpha, vx, vy, vz, age, speedExpression, speedStep, group);
               }
            }
         }

      });
   }

   public static void parameter(ParameterPayload payload, IPayloadContext context) {
      boolean polar = payload.polar();
      boolean tick = payload.tick();
      boolean rgba = payload.rgba();
      double x = payload.x();
      double y = payload.y();
      double z = payload.z();
      Vector4f color = payload.color();
      double vx = payload.vx();
      double vy = payload.vy();
      double vz = payload.vz();
      double begin = payload.begin();
      double end = payload.end();
      String expression = payload.expression();
      double step = payload.step();
      int cpt = payload.cpt();
      int age = payload.age();
      String speedExpression = payload.speedExpression();
      double speedStep = payload.speedStep();
      String group = payload.group();
      ParticleOptions effect = payload.effect();
      Minecraft.getInstance().execute(() -> {
         IExecutable exe = ExpressionUtil.parse(expression);
         if (exe == null) {
            return;
         }
         if (tick) {
            if (rgba) {
               ParticleUtil.spawnTickParticle(effect, x, y, z, vx, vy, vz, begin, end, expression, step, cpt, age, speedExpression, speedStep, group, polar);
            } else {
               ParticleUtil.spawnTickParticle(effect, x, y, z, color.x, color.y, color.z, color.w, vx, vy, vz, begin, end, expression, step, cpt, age, speedExpression, speedStep, group, polar);
            }
         } else {
            ParticleStruct data = exe.getData();
            for(double t = begin; t <= end; t += step) {
               data.t = t;
               exe.invoke();
               double dx;
               double dy;
               double dz;
               if (polar) {
                  dx = data.dis * Math.cos(data.s2) * Math.cos(data.s1);
                  dy = data.dis * Math.sin(data.s2);
                  dz = data.dis * Math.cos(data.s2) * Math.sin(data.s1);
               } else {
                  dx = data.x;
                  dy = data.y;
                  dz = data.z;
               }
               if (rgba) {
                  ParticleUtil.spawnParticle(effect, x + dx, y + dy, z + dz, x, y, z, (float)data.cr, (float)data.cg, (float)data.cb, (float)data.alpha, vx, vy, vz, age, speedExpression, speedStep, group);
               } else {
                  ParticleUtil.spawnParticle(effect, x + dx, y + dy, z + dz, x, y, z, color.x, color.y, color.z, color.w, vx, vy, vz, age, speedExpression, speedStep, group);
               }
            }
         }

      });
   }

   public static void image(ImagePayload payload, IPayloadContext context) {
      Vec3 speed = payload.speed();
      Minecraft.getInstance().execute(() -> ParticleUtil.spawnImageParticle(payload.effect(), payload.x(), payload.y(), payload.z(), payload.path(), payload.scaling(), payload.xRotate(), payload.yRotate(), payload.zRotate(), payload.flip() != 0, payload.dpb(), speed.x, speed.y, speed.z, payload.age(), payload.speedExpression(), payload.speedStep(), payload.group()));
   }

   public static void imageMatrix(ImageMatrixPayload payload, IPayloadContext context) {
      Vec3 speed = payload.speed();
      Minecraft.getInstance().execute(() -> ParticleUtil.spawnImageParticle(payload.effect(), payload.x(), payload.y(), payload.z(), payload.path(), payload.scaling(), payload.matrix(), payload.dpb(), speed.x, speed.y, speed.z, payload.age(), payload.speedExpression(), payload.speedStep(), payload.group()));
   }

   public static void video(VideoPayload payload, IPayloadContext context) {
      Vec3 speed = payload.speed();
      Minecraft.getInstance().execute(() -> ParticleUtil.spawnVideoParticle(payload.effect(), payload.x(), payload.y(), payload.z(), payload.path(), payload.scaling(), payload.xRotate(), payload.yRotate(), payload.zRotate(), payload.flip() != 0, payload.dpb(), speed.x, speed.y, speed.z, payload.age(), payload.speedExpression(), payload.speedStep(), payload.group()));
   }

   public static void videoMatrix(VideoMatrixPayload payload, IPayloadContext context) {
      Vec3 speed = payload.speed();
      Minecraft.getInstance().execute(() -> ParticleUtil.spawnVideoParticle(payload.effect(), payload.x(), payload.y(), payload.z(), payload.path(), payload.scaling(), payload.matrix(), payload.dpb(), speed.x, speed.y, speed.z, payload.age(), payload.speedExpression(), payload.speedStep(), payload.group()));
   }

   public static void groupRemove(GroupRemovePayload payload, IPayloadContext context) {
      LocalPlayer player = context.player();
      Vec3 pos = payload.pos();
      double x,y,z;
      if(pos == null){
         x = player.getX();
         y = player.getY();
         z = player.getZ();
      }else{
         x = pos.x;
         y = pos.y;
         z = pos.z;
      }
      Minecraft.getInstance().execute(() -> GroupUtil.remove(payload.group(), payload.expression(), x, y, z));
   }

   public static void groupChange(GroupChangePayload payload, IPayloadContext context) {
      int type = payload.changeType();
      String group = payload.group();
      String expression = payload.expression();
      String conditionalExpression = payload.conditionalExpression();
      LocalPlayer player = context.player();
      Vec3 pos = payload.pos();
      double x,y,z;
      if(pos == null){
         x = player.getX();
         y = player.getY();
         z = player.getZ();
      }else{
         x = pos.x;
         y = pos.y;
         z = pos.z;
      }
      Minecraft.getInstance().execute(() -> {
         IExecutable exe = ExpressionUtil.parse(expression);
         IExecutable cexe = ExpressionUtil.parse(conditionalExpression);
         Iterator<Particle> var12 = GroupUtil.get(group).iterator();

         while(true) {
            Particle particle;
            ParticleStruct data;
            double prevx;
            double prevy;
            double prevz;
            do {
               if (!var12.hasNext()) {
                  return;
               }

               particle = var12.next();
               if (!particle.isAlive()) {
                  continue;
               }
               if (cexe == null) {
                  break;
               }

               data = cexe.getData();
               prevx = ((IParticleMixin)particle).getX() - x;
               prevy = ((IParticleMixin)particle).getY() - y;
               prevz = ((IParticleMixin)particle).getZ() - z;
               data.x = prevx;
               data.y = prevy;
               data.z = prevz;
               data.s1 = Math.atan2(prevz, prevx);
               data.s2 = Math.atan2(prevy, Math.hypot(prevx, prevz));
               data.dis = Math.sqrt(prevx * prevx + prevy * prevy + prevz * prevz);
            } while(cexe.invoke() == 0);
            switch(type) {
               case 0:
                  if (exe == null) {
                     break;
                  }
                  data = exe.getData();
                  data.x = ((IParticleMixin)particle).getX() - x;
                  data.y = ((IParticleMixin)particle).getY() - y;
                  data.z = ((IParticleMixin)particle).getZ() - z;
                  prevx = data.vx = ((IParticleMixin)particle).getXd();
                  prevy = data.vy = ((IParticleMixin)particle).getYd();
                  prevz = data.vz = ((IParticleMixin)particle).getZd();
                   data.cx = ((IParticle)particle).getCenterX();
                   data.cy = ((IParticle)particle).getCenterY();
                   data.cz = ((IParticle)particle).getCenterZ();
                   if (particle instanceof IBillboardParticleMixin billboardParticle) {
                      data.cr = billboardParticle.getRCol();
                      data.cg = billboardParticle.getGCol();
                      data.cb = billboardParticle.getBCol();
                      data.alpha = billboardParticle.getAlpha();
                   } else {
                      data.cr = data.cg = data.cb = data.alpha = 1.0D;
                   }
                   exe.invoke();
                   particle.move(data.x - ((IParticleMixin)particle).getX() + x, data.y - ((IParticleMixin)particle).getY() + y, data.z - ((IParticleMixin)particle).getZ() + z);
                   ((IParticle)particle).setCenterX(data.cx);
                   ((IParticle)particle).setCenterY(data.cy);
                   ((IParticle)particle).setCenterZ(data.cz);
                   ((IParticle)particle).setRenderColor((float)data.cr, (float)data.cg, (float)data.cb, (float)data.alpha);
                  if (data.vx != prevx || data.vy != prevy || data.vz != prevz) {
                     ((IParticle)particle).setStop(data.vx == 0.0D && data.vy == 0.0D && data.vz == 0.0D);
                  }
                  ((IParticleMixin)particle).setXd(data.vx);
                  ((IParticleMixin)particle).setYd(data.vy);
                  ((IParticleMixin)particle).setZd(data.vz);
                  break;
               case 1:
                  ((IParticle)particle).setCustomMove(exe != null);
                  ((IParticle)particle).setExe(exe);
               }
            }
      });
   }

   public static int readInt(FriendlyByteBuf buf, boolean read) {
       return read ? buf.readInt() : 0;
   }

   public static double readDouble(FriendlyByteBuf buf, boolean read, double defValue) {
       return read ? buf.readDouble() : defValue;
   }

   public static String readString(FriendlyByteBuf buf, boolean read) {
       return read ? buf.readUtf() : null;
   }
}
