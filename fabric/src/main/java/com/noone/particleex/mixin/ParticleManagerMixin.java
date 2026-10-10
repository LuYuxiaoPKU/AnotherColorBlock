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

/** 旧引擎（1.20.x~1.21.8）：粒子实体由 ParticleEngine 直管（Map<RenderType, Queue<Particle>>）。mixin 注解 method/target 直写 intermediary 号（loom remap 对 yarn/mojmap 成员名解析不可靠）：method_3048=mojmap tickParticles()（yarn 同名、mojmap 源名 tickParticleList）、method_3059=mojmap tickParticle(Particle)、method_3070=Particle.tick()、class_703=Particle；1.20.1~1.21.8 号恒定。 */
@Mixin({ParticleEngine.class})
public abstract class ParticleManagerMixin {
   @Shadow
   private Map<ParticleRenderType, Queue<Particle>> particles;

   @Redirect(
      method = {"method_3059(Lnet/minecraft/class_703;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_703;method_3070()V"
)
   )
   private void redirectTickParticle(Particle particle) {
      ((IParticle)particle).customTick();
   }

   @Inject(method = {"method_3048"}, at = @At("HEAD"), cancellable = true)
   private void particleex$tick(CallbackInfo ci) {
      if (this.particles.isEmpty()) {
         ci.cancel();
      } else if (ParticleExConfig.config.ParallelParticleUpdate) {
         this.particles.values().forEach(queue -> queue.parallelStream().forEach(particle -> this.method_3059(particle)));
         this.particles.values().forEach(queue -> queue.removeIf(particle -> !particle.isAlive()));
         ci.cancel();
      }
   }

   @Shadow
   private void method_3059(Particle particle) {
   }
}
