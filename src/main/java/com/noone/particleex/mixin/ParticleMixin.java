package com.noone.particleex.mixin;

import com.noone.particleex.util.ClientMessageUtil;
import com.noone.particleex.util.IExecutable;
import com.noone.particleex.util.IParticle;
import com.noone.particleex.util.ParticleStruct;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Particle.class})
public abstract class ParticleMixin implements IParticleMixin, IParticle {
   @Unique
   private IExecutable exe;
   @Unique
   private double step;
   @Unique
   private double centerX;
   @Unique
   private double centerY;
   @Unique
   private double centerZ;
   @Unique
   private boolean customMove;
   @Unique
   private boolean stop;
   @Unique
   private double moveT;
   @Unique
   private double preX;
   @Unique
   private double preY;
   @Unique
   private double preZ;
   @Shadow
   protected double x;
   @Shadow
   protected double y;
   @Shadow
   protected double z;
    @Shadow
    protected double xd;
    @Shadow
    protected double yd;
    @Shadow
    protected double zd;
    @Unique
    private float red = 1.0F;
    @Unique
    private float green = 1.0F;
    @Unique
    private float blue = 1.0F;
    @Unique
    private float alpha = 1.0F;

   public void setExe(IExecutable exe) {
      this.exe = exe;
   }

   public void setStep(double step) {
      this.step = step;
   }

   public double getCenterX() {
      return this.centerX;
   }

   public void setCenterX(double centerX) {
      this.centerX = centerX;
   }

   public double getCenterY() {
      return this.centerY;
   }

   public void setCenterY(double centerY) {
      this.centerY = centerY;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenterZ(double centerZ) {
      this.centerZ = centerZ;
   }

   public void setCustomMove(boolean customMove) {
      this.customMove = customMove;
   }

   public void setStop(boolean stop) {
      this.stop = stop;
   }

   public void setRenderColor(float red, float green, float blue, float alpha) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
      this.syncRenderColor();
   }

   @Shadow
   public abstract void tick();

   @Shadow
   public abstract void move(double var1, double var3, double var5);

   @Shadow
   public abstract void remove();

   @Shadow
   public abstract void setPos(double var1, double var3, double var5);

   public void customTick() {
      this.preX = this.x;
      this.preY = this.y;
      this.preZ = this.z;
      this.tick();
      if (this.stop) {
         this.setPos(this.preX, this.preY, this.preZ);
      }

      this.customMove();
   }

   @Unique
   protected void customMove() {
      if (this.customMove && this.exe != null) {
         ParticleStruct data = this.exe.getData();
         if (this.moveT == 0.0D) {
            data.cx = this.centerX;
            data.cy = this.centerY;
            data.cz = this.centerZ;
            data.dx = this.x - this.centerX;
            data.dy = this.y - this.centerY;
            data.dz = this.z - this.centerZ;
            data.ddis = Math.sqrt((this.x - this.centerX) * (this.x - this.centerX) + (this.y - this.centerY) * (this.y - this.centerY) + (this.z - this.centerZ) * (this.z - this.centerZ));
            data.ds1 = Math.atan2(this.z - this.centerZ, this.x - this.centerX);
            data.ds2 = Math.atan2(this.y - this.centerY, Math.hypot(this.x - this.centerX, this.z - this.centerZ));
         }

         data.vx = Double.NaN;
         data.vy = Double.NaN;
         data.vz = Double.NaN;
         data.x = this.x - this.centerX;
         data.y = this.y - this.centerY;
         data.z = this.z - this.centerZ;
         data.cr = this.red;
         data.cg = this.green;
         data.cb = this.blue;
         data.alpha = this.alpha;
         data.dis = Math.sqrt((this.x - this.centerX) * (this.x - this.centerX) + (this.y - this.centerY) * (this.y - this.centerY) + (this.z - this.centerZ) * (this.z - this.centerZ));
         data.s1 = Math.atan2(this.z - this.centerZ, this.x - this.centerX);
         data.s2 = Math.atan2(this.y - this.centerY, Math.hypot(this.x - this.centerX, this.z - this.centerZ));
         data.t = this.moveT;
         this.moveT += this.step;

         try {
            this.exe.invoke();
         } catch (RuntimeException var3) {
            ClientMessageUtil.addChatMessage(var3);
            this.remove();
            return;
         }

          if (data.destroy != 0.0D) {
            this.remove();
            return;
         }

         if (data.vx == data.vx || data.vy == data.vy || data.vz == data.vz) {
            this.setPos(this.preX, this.preY, this.preZ);
            data.vx = this.nanToZero(data.vx);
            data.vy = this.nanToZero(data.vy);
            data.vz = this.nanToZero(data.vz);
            this.move(data.vx, data.vy, data.vz);
         }

         this.red = (float)data.cr;
         this.green = (float)data.cg;
         this.blue = (float)data.cb;
         this.alpha = (float)data.alpha;
         this.syncRenderColor();
      }

   }

   @Unique
   private void syncRenderColor() {
      if ((Object) this instanceof SingleQuadParticle billboardParticle) {
         billboardParticle.setColor(this.red, this.green, this.blue);
         ((IBillboardParticleMixin) billboardParticle).setAlpha(this.alpha);
      }
   }

   @Unique
   private double nanToZero(double num) {
      return num == num ? num : 0.0D;
   }
}
