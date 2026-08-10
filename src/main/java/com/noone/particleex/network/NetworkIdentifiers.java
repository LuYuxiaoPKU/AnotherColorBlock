package com.noone.particleex.network;

import com.noone.particleex.ParticleEx;
import net.minecraft.resources.Identifier;

public class NetworkIdentifiers {
   public static final Identifier CLEAR_PARTICLE_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "clearparticle");
   public static final Identifier CLEAR_CACHE_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "clearcache");
   public static final Identifier NORMAL_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "normal");
   public static final Identifier CONDITIONAL_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "conditional");
   public static final Identifier PARAMETER_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "parameter");
   public static final Identifier IMAGE_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "image");
   public static final Identifier IMAGE_MATRIX_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "imagematrix");
   public static final Identifier VIDEO_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "video");
   public static final Identifier VIDEO_MATRIX_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "videomatrix");
   public static final Identifier GROUP_REMOVE_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "groupremove");
   public static final Identifier GROUP_CHANGE_PACKET_ID = Identifier.fromNamespaceAndPath(ParticleEx.MOD_ID, "groupchange");
}
