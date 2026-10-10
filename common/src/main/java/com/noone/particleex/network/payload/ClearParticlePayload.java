package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClearParticlePayload() implements CustomPacketPayload {
    public static final Type<ClearParticlePayload> TYPE = new Type<>(NetworkIdentifiers.CLEAR_PARTICLE_PACKET_ID);
    @Override
  public void write(FriendlyByteBuf buf) {
  }

  public static ClearParticlePayload read(FriendlyByteBuf buf) {
    return new ClearParticlePayload();
  }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
