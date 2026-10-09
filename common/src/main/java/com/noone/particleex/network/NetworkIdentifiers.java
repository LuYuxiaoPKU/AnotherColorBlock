package com.noone.particleex.network;

import net.minecraft.resources.Identifier;

/** 全部 S2C payload 的协议 ID（Mojang 映射，common 与两端共用）。 */
public class NetworkIdentifiers {
   public static final String MOD_ID = "particleex";

   public static final Identifier CLEAR_PARTICLE_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "clearparticle");
   public static final Identifier CLEAR_CACHE_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "clearcache");
   public static final Identifier NORMAL_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "normal");
   public static final Identifier CONDITIONAL_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "conditional");
   public static final Identifier PARAMETER_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "parameter");
   public static final Identifier IMAGE_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "image");
   public static final Identifier IMAGE_MATRIX_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "imagematrix");
   public static final Identifier VIDEO_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "video");
   public static final Identifier VIDEO_MATRIX_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "videomatrix");
   public static final Identifier GROUP_REMOVE_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "groupremove");
   public static final Identifier GROUP_CHANGE_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "groupchange");
}
