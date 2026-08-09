package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ClearCachePayload() implements CustomPayload {
    public static final Id<ClearCachePayload> ID = new Id<>(NetworkIdentifiers.CLEAR_CACHE_PACKET_ID);
    public static final PacketCodec<PacketByteBuf, ClearCachePayload> CODEC = PacketCodec.unit(new ClearCachePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
