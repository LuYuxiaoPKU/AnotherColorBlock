package com.noone.particleex.util;

/** 错误/消息上报出口（平台实现：Fabric 用聊天框，NeoForge 同理，服务端用日志）。 */
public interface IMessageSink {
    void addChatMessage(Throwable throwable);
}
