package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ClearParticlePayload() implements CustomPayload {
    public static final Id<ClearParticlePayload> ID = new Id<>(NetworkIdentifiers.CLEAR_PARTICLE_PACKET_ID);
    public static final PacketCodec<PacketByteBuf, ClearParticlePayload> CODEC = PacketCodec.unit(new ClearParticlePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
