package com.noone.particleex.util;

import com.google.common.collect.Queues;
import com.noone.particleex.ParticleEx;
import com.noone.particleex.mixin.IParticleMixin;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;

public class ParticleUtil {
   private static final Queue<TimerTask> TICKSTARTTASKS = Queues.newArrayDeque();
   private static final Queue<TimerTask> TICKENDTASKS = Queues.newArrayDeque();
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

   public static Particle spawnParticle(ParticleEffect effect, double x, double y, double z, double cx, double cy, double cz, float red, float green, float blue, float alpha, double vx, double vy, double vz, int age, String expression, double step, String group) {
      try {
         Particle particle = CLIENT.particleManager.addParticle(effect, x, y, z, vx, vy, vz);
         if (particle != null) {
            particle.setColor(red, green, blue);
            ((IParticleMixin)particle).setAlpha(alpha);
            if (vx == 0.0D && vy == 0.0D && vz == 0.0D) {
               ((IParticle)particle).setStop(true);
            } else {
               ((IParticle)particle).setStop(false);
               ((IParticleMixin)particle).setVelocityX(vx);
               ((IParticleMixin)particle).setVelocityY(vy);
               ((IParticleMixin)particle).setVelocityZ(vz);
            }

            ((IParticle)particle).setCenterX(cx);
            ((IParticle)particle).setCenterY(cy);
            ((IParticle)particle).setCenterZ(cz);
            if (age > 0) {
               particle.setMaxAge(age);
            } else if (age == -1) {
               particle.setMaxAge(Integer.MAX_VALUE);
            }

            if (expression != null && !expression.equals("null")) {
               ((IParticle)particle).setExe(ExpressionUtil.parse(expression));
               ((IParticle)particle).setStep(step);
               ((IParticle)particle).setCustomMove(true);
            }

            GroupUtil.add(group, particle);
         }

         return particle;
      } catch (RuntimeException var29) {
         ClientMessageUtil.addChatMessage(var29);
         return null;
      }
   }

   public static void spawnTickParticle(ParticleEffect effect, double x, double y, double z, float red, float green, float blue, float alpha, double vx, double vy, double vz, double begin, double end, String expression, double step, int cpt, int age, String speedExpression, double speedStep, String group, boolean polar) {
      (new ParticleUtil.TickParticleTask(effect, x, y, z, red, green, blue, alpha, vx, vy, vz, begin, end, expression, step, cpt, age, speedExpression, speedStep, group, polar, false)).run();
   }

   public static void spawnTickParticle(ParticleEffect effect, double x, double y, double z, double vx, double vy, double vz, double begin, double end, String expression, double step, int cpt, int age, String speedExpression, double speedStep, String group, boolean polar) {
      (new ParticleUtil.TickParticleTask(effect, x, y, z, 1.0F, 1.0F, 1.0F, 1.0F, vx, vy, vz, begin, end, expression, step, cpt, age, speedExpression, speedStep, group, polar, true)).run();
   }

   public static void spawnImageParticle(ParticleEffect effect, double x, double y, double z, String path, double scaling, int xRotate, int yRotate, int zRotate, boolean flip, double dpb, double vx, double vy, double vz, int age, String speedExpression, double speedStep, String group) {
      spawnImageParticle(effect, x, y, z, path, scaling, xRotate, yRotate, zRotate, flip, null, dpb, vx, vy, vz, age, speedExpression, speedStep, group);
   }

   public static void spawnImageParticle(ParticleEffect effect, double x, double y, double z, String path, double scaling, double[][] matrix, double dpb, double vx, double vy, double vz, int age, String speedExpression, double speedStep, String group) {
      spawnImageParticle(effect, x, y, z, path, scaling, 0, 0, 0, false, matrix, dpb, vx, vy, vz, age, speedExpression, speedStep, group);
   }

   public static void spawnImageParticle(ParticleEffect effect, double x, double y, double z, String path, double scaling, int xRotate, int yRotate, int zRotate, boolean flip, double[][] matrix, double dpb, double vx, double vy, double vz, int age, String speedExpression, double speedStep, String group) {
      try {
         BufferedImage image = ImageUtil.readImage(path, scaling);
         int rows = image.getHeight();
         int cols = image.getWidth();
         int[][] rotateFlipMat = getRotateFlipMat(xRotate, yRotate, zRotate, flip, rows, cols);

         for(int row = 0; row < rows; ++row) {
            for(int col = 0; col < cols; ++col) {
               int pixel = image.getRGB(col, row);
               float alpha = (float)((pixel & -16777216) >>> 24) / 255.0F;
               float red = (float)((pixel & 16711680) >>> 16) / 255.0F;
               float green = (float)((pixel & '\uff00') >>> 8) / 255.0F;
               float blue = (float)(pixel & 255) / 255.0F;
               double[][] pos = MatrixUtil.matDiv(MatrixUtil.matMul(rotateFlipMat, new int[][]{{col}, {row}, {0}, {1}}), dpb);
               if (matrix != null) {
                  pos = MatrixUtil.matMul(matrix, pos);
               }

               double dx = pos[0][0];
               double dy = pos[1][0];
               double dz = pos[2][0];
               if (alpha != 0.0F) {
                  spawnParticle(effect, x + dx, y + dy, z + dz, x, y, z, red, green, blue, alpha, vx, vy, vz, age, speedExpression, speedStep, group);
               }
            }
         }
      } catch (IOException e) {
         ParticleEx.LOGGER.info(e.getMessage());
      }

   }

   public static void spawnVideoParticle(ParticleEffect effect, double x, double y, double z, String path, double scaling, int xRotate, int yRotate, int zRotate, boolean flip, double dpb, double vx, double vy, double vz, int age, String speedExpression, double speedStep, String group) {
      spawnVideoParticle(effect, x, y, z, path, scaling, xRotate, yRotate, zRotate, flip, null, dpb, vx, vy, vz, age, speedExpression, speedStep, group);
   }

   public static void spawnVideoParticle(ParticleEffect effect, double x, double y, double z, String path, double scaling, double[][] matrix, double dpb, double vx, double vy, double vz, int age, String speedExpression, double speedStep, String group) {
      spawnVideoParticle(effect, x, y, z, path, scaling, 0, 0, 0, false, matrix, dpb, vx, vy, vz, age, speedExpression, speedStep, group);
   }

   public static void spawnVideoParticle(ParticleEffect effect, double x, double y, double z, String path, double scaling, int xRotate, int yRotate, int zRotate, boolean flip, double[][] matrix, double dpb, double vx, double vy, double vz, int age, String speedExpression, double speedStep, String group) {
      VideoUtil.decoder(path, new ParticleUtil.VideoConsumer(effect, x, y, z, scaling, xRotate, yRotate, zRotate, flip, matrix, dpb, vx, vy, vz, age, speedExpression, speedStep, group));
   }

   private static int[][] getRotateFlipMat(int xRotate, int yRotate, int zRotate, boolean flip, int rows, int cols) {
      int[][] flipmat = new int[][]{{flip ? -1 : 1, 0, 0, flip ? cols - 1 : 0}, {0, -1, 0, rows - 1}, {0, 0, 1, 0}, {0, 0, 0, 1}};
      int[][] zmat = new int[][]{{dcos(zRotate), -dsin(zRotate), 0, xmove(zRotate, rows, cols)}, {dsin(zRotate), dcos(zRotate), 0, ymove(zRotate, rows, cols)}, {0, 0, 1, 0}, {0, 0, 0, 1}};
      int[][] ymat = new int[][]{{dcos(yRotate), 0, dsin(yRotate), 0}, {0, 1, 0, 0}, {-dsin(yRotate), 0, dcos(yRotate), 0}, {0, 0, 0, 1}};
      int[][] xmat = new int[][]{{1, 0, 0, 0}, {0, dcos(xRotate), dsin(xRotate), 0}, {0, -dsin(xRotate), dcos(xRotate), 0}, {0, 0, 0, 1}};
      return MatrixUtil.matMul(xmat, MatrixUtil.matMul(ymat, MatrixUtil.matMul(zmat, flipmat)));
   }

   private static int dsin(int n) {
       return switch (n % 4) {
           case 0, 2 -> 0;
           case 1 -> 1;
           case 3 -> -1;
           default -> n;
       };
   }

   private static int dcos(int n) {
       return switch (n % 4) {
           case 0 -> 1;
           case 1, 3 -> 0;
           case 2 -> -1;
           default -> n;
       };
   }

   private static int xmove(int rotate, int rows, int cols) {
       return switch (rotate % 4) {
           case 0, 3 -> 0;
           case 1 -> rows - 1;
           case 2 -> cols - 1;
           default -> rotate;
       };
   }

   private static int ymove(int rotate, int rows, int cols) {
       return switch (rotate % 4) {
           case 0, 1 -> 0;
           case 2 -> rows - 1;
           case 3 -> cols - 1;
           default -> rotate;
       };
   }

   private static void addTask(TimerTask task, boolean start) {
      if (start) {
         synchronized(TICKSTARTTASKS) {
            TICKSTARTTASKS.add(task);
         }
      } else {
         synchronized(TICKENDTASKS) {
            TICKENDTASKS.add(task);
         }
      }

   }

   public static void onStartClientTick() {
      synchronized(TICKSTARTTASKS) {
         while(!TICKSTARTTASKS.isEmpty()) {
            TICKSTARTTASKS.poll().run();
         }

      }
   }

   public static void onEndClientTick() {
      synchronized(TICKENDTASKS) {
         while(!TICKENDTASKS.isEmpty()) {
            TICKENDTASKS.poll().run();
         }

      }
   }

   private static class TickParticleTask extends TimerTask {
      private final ParticleEffect particleType;
      private final double x;
      private final double y;
      private final double z;
      private float red;
      private float green;
      private float blue;
      private float alpha;
      private double vx;
      private double vy;
      private double vz;
      private final IExecutable exe;
      private final double step;
      private final int cpt;
      private double t;
      private final double end;
      private final int age;
      private final String speedExpression;
      private final double speedStep;
      private final String group;
      private final boolean polar;
      private final boolean rgba;

      public TickParticleTask(ParticleEffect particleType, double x, double y, double z, float red, float green, float blue, float alpha, double vx, double vy, double vz, double begin, double end, String expression, double step, int cpt, int age, String speedExpression, double speedStep, String group, boolean polar, boolean rgba) {
         this.polar = polar;
         this.rgba = rgba;
         this.particleType = particleType;
         this.x = x;
         this.y = y;
         this.z = z;
         this.t = begin;
         this.end = end;
         this.exe = ExpressionUtil.parse(expression);
         this.step = step;
         this.cpt = cpt;
         this.age = age;
         this.speedExpression = speedExpression;
         this.speedStep = speedStep;
         this.group = group;
         if (!rgba) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
         }

      }
      public void run() {
         ParticleStruct data = this.exe.getData();
         for(int i = 0; i < this.cpt && this.t <= this.end; this.t += this.step) {
            data.t = this.t;
            this.exe.invoke();
            double dx;
            double dy;
            double dz;
            if (this.polar) {
               dx = data.dis * Math.cos(data.s2) * Math.cos(data.s1);
               dy = data.dis * Math.sin(data.s2);
               dz = data.dis * Math.cos(data.s2) * Math.sin(data.s1);
            } else {
               dx = data.x;
               dy = data.y;
               dz = data.z;
            }

            if (this.rgba) {
               double vx = data.vx;
               double vy = data.vy;
               double vz = data.vz;
               ParticleUtil.spawnParticle(this.particleType, this.x + dx, this.y + dy, this.z + dz, this.x, this.y, this.z, (float)data.cr, (float)data.cg, (float)data.cb, (float)data.alpha, vx, vy, vz, this.age, this.speedExpression, this.speedStep, this.group);
            } else {
               ParticleUtil.spawnParticle(this.particleType, this.x + dx, this.y + dy, this.z + dz, this.x, this.y, this.z, this.red, this.green, this.blue, this.alpha, this.vx, this.vy, this.vz, this.age, this.speedExpression, this.speedStep, this.group);
            }

            ++i;
         }

         if (this.t <= this.end) {
            ParticleUtil.addTask(new ParticleUtil.TickEndTask(this), false);
         }

      }
   }

   private static class VideoConsumer implements Predicate<BufferedImage> {
      private final ParticleEffect effect;
      private final double x;
      private final double y;
      private final double z;
      private final double scaling;
      private final int xRotate;
      private final int yRotate;
      private final int zRotate;
      private final boolean flip;
      private final double[][] matrix;
      private final double dpb;
      private final double vx;
      private final double vy;
      private final double vz;
      private final int age;
      private final String speedExpression;
      private final double speedStep;
      private final String group;
      private int init;
      private Particle[][] particles;
      private final CountDownLatch initLatch = new CountDownLatch(1);

      private VideoConsumer(ParticleEffect effect, double x, double y, double z, double scaling, int xRotate, int yRotate, int zRotate, boolean flip, double[][] matrix, double dpb, double vx, double vy, double vz, int age, String speedExpression, double speedStep, String group) {
         this.effect = effect;
         this.x = x;
         this.y = y;
         this.z = z;
         this.scaling = scaling;
         this.xRotate = xRotate;
         this.yRotate = yRotate;
         this.zRotate = zRotate;
         this.flip = flip;
         this.matrix = matrix;
         this.dpb = dpb;
         this.vx = vx;
         this.vy = vy;
         this.vz = vz;
         this.age = age;
         this.speedExpression = speedExpression;
         this.speedStep = speedStep;
         this.group = group;
      }

      public boolean test(BufferedImage image) {
         int width = image.getWidth();
         int height = image.getHeight();
         int dw = (int)((double)width * this.scaling);
         int dh = (int)((double)height * this.scaling);
         BufferedImage resultImage = new BufferedImage(dw, dh, image.getType());
         Graphics2D graphics = resultImage.createGraphics();
         graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         graphics.drawImage(image, 0, 0, dw, dh, 0, 0, width, height, null);
         graphics.dispose();
         int[][] rotateFlipMat = ParticleUtil.getRotateFlipMat(this.xRotate, this.yRotate, this.zRotate, this.flip, dh, dw);
         if (this.init == 0) {
            this.init = 1;
            this.particles = new Particle[dh][dw];
            ParticleUtil.CLIENT.execute(() -> {
               for(int row = 0; row < dh; ++row) {
                  for(int col = 0; col < dw; ++col) {
                     int pixel = resultImage.getRGB(col, row);
                     float red = (float)((pixel & 16711680) >>> 16) / 255.0F;
                     float green = (float)((pixel & '\uff00') >>> 8) / 255.0F;
                     float blue = (float)(pixel & 255) / 255.0F;
                     double[][] pos = MatrixUtil.matDiv(MatrixUtil.matMul(rotateFlipMat, new int[][]{{col}, {row}, {0}, {1}}), this.dpb);
                     if (this.matrix != null) {
                        pos = MatrixUtil.matMul(this.matrix, pos);
                     }

                     double dx = pos[0][0];
                     double dy = pos[1][0];
                     double dz = pos[2][0];
                     this.particles[row][col] = ParticleUtil.spawnParticle(this.effect, this.x + dx, this.y + dy, this.z + dz, this.x, this.y, this.z, red, green, blue, 1.0F, this.vx, this.vy, this.vz, this.age, this.speedExpression, this.speedStep, this.group);
                  }
               }

               this.init = 2;
               this.initLatch.countDown();
            });
            return true;
         } else {
            try {
               this.initLatch.await();
            } catch (InterruptedException e) {
               ParticleEx.LOGGER.info(e.getMessage());
               return false;
            }

            boolean alive = false;

            for(int row = 0; row < dh; ++row) {
               for(int col = 0; col < dw; ++col) {
                  if (this.particles[row][col].isAlive()) {
                     int pixel = resultImage.getRGB(col, row);
                     float red = (float)((pixel & 16711680) >>> 16) / 255.0F;
                     float green = (float)((pixel & '\uff00') >>> 8) / 255.0F;
                     float blue = (float)(pixel & 255) / 255.0F;
                     this.particles[row][col].setColor(red, green, blue);
                     alive = true;
                  }
               }
            }

            return alive;
         }
      }
   }

   private static class TickEndTask extends TimerTask {
      private final TimerTask nextTask;

      public TickEndTask(TimerTask nextTask) {
         this.nextTask = nextTask;
      }

      public void run() {
         ParticleUtil.addTask(this.nextTask, true);
      }
   }
}
