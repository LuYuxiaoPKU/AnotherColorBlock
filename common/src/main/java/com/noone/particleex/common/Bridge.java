package com.noone.particleex.common;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;

/** 平台桥：平台模块初始化时注入实现（Fabric 用 PlayerLookup+ServerPlayNetworking，NeoForge 用 PacketDistributor）。 */
public final class Bridge {
    private static IPayloadSender sender = (world, payload) -> {};

    private Bridge() {}

    public static void setSender(IPayloadSender s) {
        sender = s;
    }

    /** 向世界内所有在线玩家广播 payload（服务端命令执行入口）。 */
    public static void sendToPlayers(ServerLevel world, CustomPacketPayload payload) {
        sender.send(world, payload);
    }

    public interface IPayloadSender {
        void send(ServerLevel world, CustomPacketPayload payload);
    }
}