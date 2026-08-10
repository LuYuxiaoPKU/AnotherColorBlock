package com.noone.particleex.mixin;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleLimit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ParticleEngine.class})
public interface ParticleManagerAccessor {
   @Invoker("updateCount")
   void invokeAddTo(ParticleLimit group, int count);
}
