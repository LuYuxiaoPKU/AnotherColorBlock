package com.noone.particleex.mixin;

import com.noone.particleex.ParticleExConfig;
import com.noone.particleex.util.IParticle;
import java.util.Map;
import java.util.Queue;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 旧引擎（1.20.6/1.21.1）：粒子实体由 ParticleEngine 直管（Map<RenderType, Queue<Particle>>），无 ParticleGroup/ParticleLimit/容量注入点。 */
@Mixin({ParticleEngine.class})
public abstract class ParticleManagerMixin {
   @Shadow
   private Map<ParticleRenderType, Queue<Particle>> particles;

   @Redirect(
      method = {"tickParticle(Lnet/minecraft/client/particle/Particle;)V"},
      at = @At(
   value = "INVOKE",
   target = "net.minecraft.client.particle.Particle.tick()V"
)
   )
   private void redirectTickParticle(Particle particle) {
      ((IParticle)particle).customTick();
   }

   @Inject(method = {"tickParticles"}, at = @At("HEAD"), cancellable = true)
   private void particleex$tick(CallbackInfo ci) {
      if (this.particles.isEmpty()) {
         ci.cancel();
      } else if (ParticleExConfig.config.ParallelParticleUpdate) {
         this.particles.values().forEach(queue -> queue.parallelStream().forEach(particle -> this.tickParticle(particle)));
         this.particles.values().forEach(queue -> queue.removeIf(particle -> !particle.isAlive()));
         ci.cancel();
      }
   }

   @Shadow
   private void tickParticle(Particle particle) {
   }
}
