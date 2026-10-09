package com.noone.particleex.util;

/** 平台桥：平台模块初始化时注入实现（客户端聊天框/服务端日志）。 */
public final class MessageBridge {
    private static IMessageSink sink = throwable -> throwable.printStackTrace();

    private MessageBridge() {}

    public static void setSink(IMessageSink s) {
        sink = s;
    }

    public static void report(Throwable t) {
        sink.addChatMessage(t);
    }
}
