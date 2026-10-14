package com.noone.particleex.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** rCol/gCol/bCol/alpha 定义在 Particle（非 SingleQuadParticle）；mixin 运行时按 target 类为 owner 解析 @Accessor 字段，target 必须是字段 owner 才能命中（1.20.6 字节码实证 alpha/rCol 在 Particle；与 fabric 侧 6482461 同根因）。 */
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