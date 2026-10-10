package com.noone.particleex.network;

import net.minecraft.resources.ResourceLocation;

/** 全部 S2C payload 的协议 ID（Mojang 映射，common 与两端共用）。 */
public class NetworkIdentifiers {
   public static final String MOD_ID = "particleex";

   public static final ResourceLocation CLEAR_PARTICLE_PACKET_ID = ResourceLocation.of(MOD_ID, "clearparticle");
   public static final ResourceLocation CLEAR_CACHE_PACKET_ID = ResourceLocation.of(MOD_ID, "clearcache");
   public static final ResourceLocation NORMAL_PACKET_ID = ResourceLocation.of(MOD_ID, "normal");
   public static final ResourceLocation CONDITIONAL_PACKET_ID = ResourceLocation.of(MOD_ID, "conditional");
   public static final ResourceLocation PARAMETER_PACKET_ID = ResourceLocation.of(MOD_ID, "parameter");
   public static final ResourceLocation IMAGE_PACKET_ID = ResourceLocation.of(MOD_ID, "image");
   public static final ResourceLocation IMAGE_MATRIX_PACKET_ID = ResourceLocation.of(MOD_ID, "imagematrix");
   public static final ResourceLocation VIDEO_PACKET_ID = ResourceLocation.of(MOD_ID, "video");
   public static final ResourceLocation VIDEO_MATRIX_PACKET_ID = ResourceLocation.of(MOD_ID, "videomatrix");
   public static final ResourceLocation GROUP_REMOVE_PACKET_ID = ResourceLocation.of(MOD_ID, "groupremove");
   public static final ResourceLocation GROUP_CHANGE_PACKET_ID = ResourceLocation.of(MOD_ID, "groupchange");
}
