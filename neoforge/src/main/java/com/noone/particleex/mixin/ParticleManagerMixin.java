package com.noone.particleex.mixin;

import com.noone.particleex.ParticleExConfig;
import com.noone.particleex.util.IParticle;
import java.util.Queue;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21.9+：粒子批处理由 ParticleGroup 直管（方法随结构从 ParticleEngine 迁出）。NeoForge 运行时为 mojmap 空间，注解直写 mojmap 名。 */
@Mixin({ParticleGroup.class})
public abstract class ParticleManagerMixin {
   @Shadow
   private Queue<Particle> particles;

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
         this.particles.parallelStream().forEach(particle -> this.tickParticle(particle));
         this.particles.removeIf(particle -> !particle.isAlive());
         ci.cancel();
      }
   }

   @Shadow
   private void tickParticle(Particle particle) {
   }
}