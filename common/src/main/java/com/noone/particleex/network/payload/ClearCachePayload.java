package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClearCachePayload() implements CustomPacketPayload {
    @Override
  public void write(FriendlyByteBuf buf) {
  }

  public static ClearCachePayload read(FriendlyByteBuf buf) {
    return new ClearCachePayload();
  }

  @Override
  public net.minecraft.resources.ResourceLocation id() {
    return NetworkIdentifiers.CLEARCACHE_PACKET_ID;
  }
}
