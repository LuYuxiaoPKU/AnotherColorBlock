package com.noone.particleex.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Particle.class})
public interface IParticleMixin {
   @Accessor("x")
   double getX();

   @Accessor("y")
   double getY();

   @Accessor("z")
   double getZ();

   @Accessor
   double getVelocityX();

   @Accessor
   void setVelocityX(double var1);

   @Accessor
   double getVelocityY();

   @Accessor
   void setVelocityY(double var1);

   @Accessor
   double getVelocityZ();

   @Accessor
   void setVelocityZ(double var1);

   @Accessor
   int getAge();

   @Accessor
   float getRed();

   @Accessor
   float getGreen();

   @Accessor
   float getBlue();

   @Accessor
   float getAlpha();

   @Accessor("alpha")
   void setAlpha(float var1);
}
