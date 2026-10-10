package com.noone.particleex.network;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

/** Payload 编解码辅助（原 ClientNetworkHandler 的静态读写工具，供 common 复用）。 */
public final class PayloadCodecUtil {
    private PayloadCodecUtil() {}

    public static int readInt(FriendlyByteBuf buf, boolean read) {
        return read ? buf.readInt() : 0;
    }

    public static double readDouble(FriendlyByteBuf buf, boolean read, double defValue) {
        return read ? buf.readDouble() : defValue;
    }

    public static String readString(FriendlyByteBuf buf, boolean read) {
        return read ? buf.readUtf() : null;
    }

    /** 读粒子：注册表 ID + Deserializer（1.20.x 无 ParticleOptions.fromNetwork 静态方法） */
    public static ParticleOptions readParticle(FriendlyByteBuf buf) {
        ParticleType<?> type = buf.readById(net.minecraft.core.registries.BuiltInRegistries.PARTICLE_TYPE);
        return readParticleFromType(buf, type);
    }

    private static <T extends ParticleOptions> T readParticleFromType(FriendlyByteBuf buf, ParticleType<T> type) {
        return type.getDeserializer().fromNetwork(type, buf);
    }
}
