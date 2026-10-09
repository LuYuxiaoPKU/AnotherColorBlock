package com.noone.particleex.mixin;

import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SingleQuadParticle.class})
public interface IBillboardParticleMixin {
   @Accessor
   float getRCol();

   @Accessor
   float getGCol();

   @Accessor
   float getBCol();

   @Accessor
   float getAlpha();

   @Accessor("alpha")
   void setAlpha(float var1);
}