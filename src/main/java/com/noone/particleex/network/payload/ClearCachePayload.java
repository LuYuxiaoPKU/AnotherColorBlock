package com.noone.particleex.network.payload;

import com.noone.particleex.network.NetworkIdentifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClearCachePayload() implements CustomPacketPayload {
    public static final Type<ClearCachePayload> ID = new Type<>(NetworkIdentifiers.CLEAR_CACHE_PACKET_ID);
    public static final StreamCodec<FriendlyByteBuf, ClearCachePayload> CODEC = StreamCodec.unit(new ClearCachePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
