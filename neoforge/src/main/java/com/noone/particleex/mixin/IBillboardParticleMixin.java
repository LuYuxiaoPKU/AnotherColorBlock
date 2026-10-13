package com.noone.particleex.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** rCol/gCol/bCol/alpha 定义在 Particle（非 SingleQuadParticle）；mixin 运行时按 target 类为 owner 解析 @Accessor 字段，target 必须是字段 owner 才能命中（1.21.9+ 字段才在 SingleQuadParticle 自身；与 fabric 侧 99840c6 同根因）。 */
@Mixin({Particle.class})
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