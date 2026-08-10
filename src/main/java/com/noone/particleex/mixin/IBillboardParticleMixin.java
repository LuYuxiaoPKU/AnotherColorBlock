package com.noone.particleex.mixin;

import net.minecraft.client.particle.BillboardParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BillboardParticle.class})
public interface IBillboardParticleMixin {
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
