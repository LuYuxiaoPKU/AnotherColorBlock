package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;

public record ClearParticlePayload() implements com.noone.particleex.network.ParticlePayload {
    @Override
  public void write(FriendlyByteBuf buf) {
  }

  public static ClearParticlePayload read(FriendlyByteBuf buf) {
    return new ClearParticlePayload();
  }

}
