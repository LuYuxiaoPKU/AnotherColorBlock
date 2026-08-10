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
   double getXd();

   @Accessor
   void setXd(double var1);

   @Accessor
   double getYd();

   @Accessor
   void setYd(double var1);

   @Accessor
   double getZd();

   @Accessor
   void setZd(double var1);

   @Accessor
   int getAge();
}
