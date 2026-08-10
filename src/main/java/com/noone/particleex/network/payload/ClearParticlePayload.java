package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClearParticlePayload() implements CustomPacketPayload {
    public static final Type<ClearParticlePayload> ID = new Type<>(NetworkIdentifiers.CLEAR_PARTICLE_PACKET_ID);
    public static final StreamCodec<FriendlyByteBuf, ClearParticlePayload> CODEC = StreamCodec.unit(new ClearParticlePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
