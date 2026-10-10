package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClearCachePayload() implements CustomPacketPayload {
    public static final Type<ClearCachePayload> TYPE = new Type<>(NetworkIdentifiers.CLEAR_CACHE_PACKET_ID);
    @Override
  public void write(FriendlyByteBuf buf) {
  }

  public static ClearCachePayload read(FriendlyByteBuf buf) {
    return new ClearCachePayload();
  }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
