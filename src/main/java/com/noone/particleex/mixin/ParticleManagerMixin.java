package com.noone.particleex.mixin;

import com.noone.particleex.ParticleExConfig;
import com.noone.particleex.util.IParticle;
import java.util.Queue;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ParticleRenderer.class})
public abstract class ParticleManagerMixin {
   @Shadow
   private Queue<Particle> particles;

   @Shadow
   private ParticleManager particleManager;

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

   @ModifyArg(method = {"<init>"}, at = @At(value = "INVOKE", target = "com.google.common.collect.EvictingQueue.create(I)Lcom/google/common/collect/EvictingQueue;", remap = false))
   private static int modifyArgTick(int maxParticleCount) {
      return ParticleExConfig.config.maxParticleCount;
   }

   @Inject(method = {"tick"}, at = @At("HEAD"), cancellable = true)
   private void particleex$tick(CallbackInfo ci) {
      if (this.particles.isEmpty()) {
         ci.cancel();
      } else if (ParticleExConfig.config.ParallelParticleUpdate) {
         this.particles.parallelStream().forEach(particle -> this.tickParticle(particle));
         this.particles.removeIf(particle -> {
            if (!particle.isAlive()) {
               particle.getGroup().ifPresent(group -> ((ParticleManagerAccessor) this.particleManager).invokeAddTo(group, -1));
               return true;
            }
            return false;
         });
         ci.cancel();
      }
   }

   @Shadow
   private void tickParticle(Particle particle) {
   }
}
