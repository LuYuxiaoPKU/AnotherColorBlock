package com.noone.particleex.mixin;

import com.noone.particleex.ParticleExConfig;
import com.noone.particleex.util.IParticle;
import java.util.Queue;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 对应 Fabric 端 ParticleRenderer（Yarn）/ParticleGroup（Mojang）注入：表达式 tick、上限、并行更新。 */
@Mixin({ParticleGroup.class})
public abstract class ParticleManagerMixin {
   @Shadow
   private Queue<Particle> particles;

   @Shadow
   private ParticleEngine engine;

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

   // 26.x 专属：构造器粒子上限注入点已从 Guava EvictingQueue.create 改为
   // java.util.ArrayDeque.<init>(I)（26.x ParticleGroup 移除 Guava 队列；字节码实证：
   // 1.21.11 仍为 EvictingQueue.create(16384)，26.2 为 ArrayDeque(16384)）。
   // 注意 MemberInfo 语法：方法描述符直接连在名称后（`<init>(I)...` 而非 `<init>:(I)...`——
   // 后者 `(` 优先解析导致冒号残留在名称里，运行时 Invalid name 崩溃）
   @ModifyArg(method = {"<init>"}, at = @At(value = "INVOKE", target = "java/util/ArrayDeque.<init>(I)V", remap = false))
   private static int modifyArgTick(int maxParticleCount) {
      return ParticleExConfig.config.maxParticleCount;
   }

   // 26.x 专属：粒子上限逻辑在 add() 内（字节码实证：size>=16384 拒绝；size>=12288
   // 概率拒绝，阈值 (16384-size)/4096f）。@ModifyConstant 把 16384/12288/4096f
   // 替换为配置派生值（maxParticleCount、75%、25%）。
   @ModifyConstant(method = {"add(Lnet/minecraft/client/particle/Particle;)Z"},
         constant = {@Constant(intValue = 16384, ordinal = 0), @Constant(intValue = 12288), @Constant(intValue = 16384, ordinal = 1)})
   private static int modifyAddInt(int original) {
      int max = ParticleExConfig.config.maxParticleCount;
      return original == 16384 ? max : (int) (max * 0.75f);
   }

   @ModifyConstant(method = {"add(Lnet/minecraft/client/particle/Particle;)Z"},
         constant = @Constant(floatValue = 4096f))
   private static float modifyAddFloat(float original) {
      return ParticleExConfig.config.maxParticleCount * 0.25f;
   }

   @Inject(method = {"tickParticles"}, at = @At("HEAD"), cancellable = true)
   private void particleex$tick(CallbackInfo ci) {
      if (this.particles.isEmpty()) {
         ci.cancel();
      } else if (ParticleExConfig.config.ParallelParticleUpdate) {
         this.particles.parallelStream().forEach(particle -> this.tickParticle(particle));
         this.particles.removeIf(particle -> {
            if (!particle.isAlive()) {
               particle.getParticleLimit().ifPresent(group -> ((ParticleManagerAccessor) this.engine).invokeAddTo(group, -1));
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