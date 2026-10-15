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

/**
 * 旧引擎（1.20.x~1.21.1）：粒子实体由 ParticleEngine 直管（Map<RenderType, Queue<Particle>>），无 ParticleGroup/ParticleLimit/容量注入点。
 * 1.20.1 特例：Forge 47 体系运行时成员名是 SRG（非 mojmap），无 refmap 时注解按字面名解析——全部直写 SRG 名
 * （官方 mappings 双链实证：mojmap tickParticleList→obf a→m_107384_；tickParticle→b→m_107393_；
 * Particle.tick→a→m_5989_；particles→g→f_107289_）。
 */
@Mixin({ParticleEngine.class})
public abstract class ParticleManagerMixin {
   @Shadow
   private Map<ParticleRenderType, Queue<Particle>> f_107289_;

   @Redirect(
      method = {"m_107393_(Lnet/minecraft/client/particle/Particle;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/particle/Particle;m_5989_()V"
)
   )
   private void redirectTickParticle(Particle particle) {
      ((IParticle)particle).customTick();
   }

   @Inject(method = {"m_107384_"}, at = @At("HEAD"), cancellable = true)
   private void particleex$tick(CallbackInfo ci) {
      if (this.f_107289_.isEmpty()) {
         ci.cancel();
      } else if (ParticleExConfig.config.ParallelParticleUpdate) {
         this.f_107289_.values().forEach(queue -> queue.parallelStream().forEach(particle -> this.m_107393_(particle)));
         this.f_107289_.values().forEach(queue -> queue.removeIf(particle -> !particle.isAlive()));
         ci.cancel();
      }
   }

   @Shadow
   private void m_107393_(Particle particle) {
   }
}
