package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;

public record ClearCachePayload() implements com.noone.particleex.network.ParticlePayload {
    @Override
  public void write(FriendlyByteBuf buf) {
  }

  public static ClearCachePayload read(FriendlyByteBuf buf) {
    return new ClearCachePayload();
  }

  @Override
  public net.minecraft.resources.ResourceLocation packetId() {
    return NetworkIdentifiers.CLEAR_CACHE_PAYLOAD_PACKET_ID;
  }
}
