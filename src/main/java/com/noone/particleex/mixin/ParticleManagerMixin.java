package com.noone.particleex.mixin;

import com.noone.particleex.ParticleExConfig;
import com.noone.particleex.util.IParticle;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ParticleManager.class})
public abstract class ParticleManagerMixin {
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

   @ModifyArg(method = {"method_18125"}, at = @At(value = "INVOKE", target = "com.google.common.collect.EvictingQueue.create(I)Lcom/google/common/collect/EvictingQueue;", remap = false))
   private static int modifyArgTick(int maxParticleCount) {
      return ParticleExConfig.config.maxParticleCount;
   }


   @Shadow
   private void tickParticle(Particle particle) {
   }

   /**
    * @author Another Era
    * @reason This method is overwritten to improve particle update performance by allowing parallel processing based on configuration settings.
    */
   @Overwrite
   private void tickParticles(Collection<Particle> collection) {
      if (!collection.isEmpty()) {
         if (ParticleExConfig.config.ParallelParticleUpdate) {
            collection.parallelStream().forEach(this::tickParticle);
            collection.removeIf((particle) -> !particle.isAlive());
         } else {
            Iterator<Particle> iterator = collection.iterator();

            while(iterator.hasNext()) {
               Particle particle = iterator.next();
               this.tickParticle(particle);
               if (!particle.isAlive()) {
                  iterator.remove();
               }
            }
         }
      }
   }
}
