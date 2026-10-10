package com.noone.particleex.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/** 1.20.1 无 CustomPacketPayload（1.20.2+），平台侧以本接口统一 payload 形状。 */
public interface ParticlePayload {
    void write(FriendlyByteBuf buf);

    ResourceLocation packetId();
}
