package com.noone.particleex.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.20.1 特例：rCol/gCol/bCol/alpha 声明在父类 Particle（target 必须是字段 owner），
 * 且 Forge 47 运行时字段名为 SRG——直写 SRG 号
 * （官方 mappings 双链：rCol→v→f_107227_、gCol→w→f_107228_、bCol→x→f_107229_、alpha→y→f_107230_）。 */
@Mixin({Particle.class})
public interface IBillboardParticleMixin {
   @Accessor("f_107227_")
   float getRCol();

   @Accessor("f_107228_")
   float getGCol();

   @Accessor("f_107229_")
   float getBCol();

   @Accessor("f_107230_")
   float getAlpha();

   @Accessor("f_107230_")
   void setAlpha(float var1);
}
