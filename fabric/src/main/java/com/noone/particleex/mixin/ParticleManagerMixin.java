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

/** 1.21.9+：粒子批处理由 ParticleGroup 直管（方法随结构从 ParticleEngine 迁出）。mixin 注解 method/target 直写 intermediary 号：method_74288=mojmap tickParticle(Particle)、method_74287=mojmap tickParticles()、method_3070=Particle.tick()、class_703=Particle（loom remap 对 mojmap target 解析不可靠，产物与运行时空间直配）。 */
@Mixin({ParticleGroup.class})
public abstract class ParticleManagerMixin {
   @Shadow
   private Queue<Particle> particles;

   @Redirect(
      method = {"method_74288(Lnet/minecraft/class_703;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_703;method_3070()V"
)
   )
   private void redirectTickParticle(Particle particle) {
      ((IParticle)particle).customTick();
   }

   @Inject(method = {"method_74287"}, at = @At("HEAD"), cancellable = true)
   private void particleex$tick(CallbackInfo ci) {
      if (this.particles.isEmpty()) {
         ci.cancel();
      } else if (ParticleExConfig.config.ParallelParticleUpdate) {
         this.particles.parallelStream().forEach(particle -> this.method_74288(particle));
         this.particles.removeIf(particle -> !particle.isAlive());
         ci.cancel();
      }
   }

   @Shadow
   private void method_74288(Particle particle) {
   }
}