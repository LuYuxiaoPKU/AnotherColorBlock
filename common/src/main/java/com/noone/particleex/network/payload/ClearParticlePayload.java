package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;

public class ClearParticlePayload implements com.noone.particleex.network.ParticlePayload {
  public ClearParticlePayload() {
  }
    @Override
  public void write(FriendlyByteBuf buf) {
  }

  public static ClearParticlePayload read(FriendlyByteBuf buf) {
    return new ClearParticlePayload();
  }

  @Override
  public net.minecraft.resources.ResourceLocation packetId() {
    return NetworkIdentifiers.CLEAR_PARTICLE_PACKET_ID;
  }
}
