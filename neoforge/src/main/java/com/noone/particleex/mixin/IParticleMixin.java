package com.noone.particleex.mixin;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.20.1 特例：Forge 47 运行时字段名为 SRG，无 refmap 时 @Accessor 按字面名解析——直写 SRG 号
 * （官方 mappings 双链：x→g→f_107212_、y→h→f_107213_、z→i→f_107214_、xd→j→f_107215_、
 * yd→k→f_107216_、zd→l→f_107217_、age→s→f_107224_）。 */
@Mixin({Particle.class})
public interface IParticleMixin {
   @Accessor("f_107212_")
   double getX();

   @Accessor("f_107213_")
   double getY();

   @Accessor("f_107214_")
   double getZ();

   @Accessor("f_107215_")
   double getXd();

   @Accessor("f_107215_")
   void setXd(double var1);

   @Accessor("f_107216_")
   double getYd();

   @Accessor("f_107216_")
   void setYd(double var1);

   @Accessor("f_107217_")
   double getZd();

   @Accessor("f_107217_")
   void setZd(double var1);

   @Accessor("f_107224_")
   int getAge();
}
