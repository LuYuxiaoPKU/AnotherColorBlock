package com.noone.particleex.mixin;

import com.noone.particleex.util.IExecutable;
import com.noone.particleex.util.IParticle;
import com.noone.particleex.util.MessageBridge;
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
   protected double f_107212_;
   @Shadow
   protected double f_107213_;
   @Shadow
   protected double f_107214_;
   @Shadow
   protected double f_107215_;
   @Shadow
   protected double f_107216_;
   @Shadow
   protected double f_107217_;
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
   public abstract void m_5989_();

   @Shadow
   public abstract void m_6257_(double var1, double var3, double var5);

   @Shadow
   public abstract void m_107274_();

   @Shadow
   public abstract void m_107264_(double var1, double var3, double var5);

   public void customTick() {
      this.preX = this.f_107212_;
      this.preY = this.f_107213_;
      this.preZ = this.f_107214_;
      this.m_5989_();
      if (this.stop) {
         this.m_107264_(this.preX, this.preY, this.preZ);
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
            data.dx = this.f_107212_ - this.centerX;
            data.dy = this.f_107213_ - this.centerY;
            data.dz = this.f_107214_ - this.centerZ;
            data.ddis = Math.sqrt((this.f_107212_ - this.centerX) * (this.f_107212_ - this.centerX) + (this.f_107213_ - this.centerY) * (this.f_107213_ - this.centerY) + (this.f_107214_ - this.centerZ) * (this.f_107214_ - this.centerZ));
            data.ds1 = Math.atan2(this.f_107214_ - this.centerZ, this.f_107212_ - this.centerX);
            data.ds2 = Math.atan2(this.f_107213_ - this.centerY, Math.hypot(this.f_107212_ - this.centerX, this.f_107214_ - this.centerZ));
         }

         data.vx = Double.NaN;
         data.vy = Double.NaN;
         data.vz = Double.NaN;
         data.x = this.f_107212_ - this.centerX;
         data.y = this.f_107213_ - this.centerY;
         data.z = this.f_107214_ - this.centerZ;
         data.cr = this.red;
         data.cg = this.green;
         data.cb = this.blue;
         data.alpha = this.alpha;
         data.dis = Math.sqrt((this.f_107212_ - this.centerX) * (this.f_107212_ - this.centerX) + (this.f_107213_ - this.centerY) * (this.f_107213_ - this.centerY) + (this.f_107214_ - this.centerZ) * (this.f_107214_ - this.centerZ));
         data.s1 = Math.atan2(this.f_107214_ - this.centerZ, this.f_107212_ - this.centerX);
         data.s2 = Math.atan2(this.f_107213_ - this.centerY, Math.hypot(this.f_107212_ - this.centerX, this.f_107214_ - this.centerZ));
         data.t = this.moveT;
         this.moveT += this.step;

         try {
            this.exe.invoke();
         } catch (RuntimeException var3) {
            MessageBridge.report(var3);
            this.m_107274_();
            return;
         }

         if (data.destroy != 0.0D) {
            this.m_107274_();
            return;
         }

         if (data.vx == data.vx || data.vy == data.vy || data.vz == data.vz) {
            this.m_107264_(this.preX, this.preY, this.preZ);
            data.vx = this.nanToZero(data.vx);
            data.vy = this.nanToZero(data.vy);
            data.vz = this.nanToZero(data.vz);
            this.m_6257_(data.vx, data.vy, data.vz);
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
      if ((Object) this instanceof SingleQuadParticle quad) {
         quad.m_107253_(this.red, this.green, this.blue);
         ((IBillboardParticleMixin) quad).setAlpha(this.alpha);
      }
   }

   @Unique
   private double nanToZero(double num) {
      return num == num ? num : 0.0D;
   }
}