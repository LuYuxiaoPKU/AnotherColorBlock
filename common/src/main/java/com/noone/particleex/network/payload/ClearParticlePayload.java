package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClearParticlePayload() implements CustomPacketPayload {
    @Override
  public void write(FriendlyByteBuf buf) {
  }

  public static ClearParticlePayload read(FriendlyByteBuf buf) {
    return new ClearParticlePayload();
  }

  @Override
  public net.minecraft.resources.ResourceLocation id() {
    return NetworkIdentifiers.CLEAR_PARTICLE_PACKET_ID;
  }
}
