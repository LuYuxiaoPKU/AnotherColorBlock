package com.noone.particleex;

import com.noone.particleex.network.ClientNetworkHandler;
import com.noone.particleex.network.payload.*;
import com.noone.particleex.util.ClientMessageUtil;
import com.noone.particleex.util.MessageBridge;
import com.noone.particleex.util.ParticleUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ParticleExClient implements ClientModInitializer {
   public void onInitializeClient() {
      // 错误上报 → 客户端聊天框（仅客户端入口注册，服务器端无 net.minecraft.client 类）
      MessageBridge.setSink(new ClientMessageUtil());
      ClientTickEvents.START_CLIENT_TICK.register(client -> ParticleUtil.onStartClientTick());
      ClientTickEvents.END_CLIENT_TICK.register(client -> ParticleUtil.onEndClientTick());
      ClientPlayNetworking.registerGlobalReceiver(ClearParticlePayload.TYPE, (payload, context) -> ClientNetworkHandler.clearParticle(context));
      ClientPlayNetworking.registerGlobalReceiver(ClearCachePayload.TYPE, (payload, context) -> ClientNetworkHandler.clearCache(context));
      ClientPlayNetworking.registerGlobalReceiver(NormalPayload.TYPE, ClientNetworkHandler::normal);
      ClientPlayNetworking.registerGlobalReceiver(ConditionalPayload.TYPE, ClientNetworkHandler::conditional);
      ClientPlayNetworking.registerGlobalReceiver(ParameterPayload.TYPE, ClientNetworkHandler::parameter);
      ClientPlayNetworking.registerGlobalReceiver(ImagePayload.TYPE, ClientNetworkHandler::image);
      ClientPlayNetworking.registerGlobalReceiver(ImageMatrixPayload.TYPE, ClientNetworkHandler::imageMatrix);
      ClientPlayNetworking.registerGlobalReceiver(VideoPayload.TYPE, ClientNetworkHandler::video);
      ClientPlayNetworking.registerGlobalReceiver(VideoMatrixPayload.TYPE, ClientNetworkHandler::videoMatrix);
      ClientPlayNetworking.registerGlobalReceiver(GroupRemovePayload.TYPE, ClientNetworkHandler::groupRemove);
      ClientPlayNetworking.registerGlobalReceiver(GroupChangePayload.TYPE, ClientNetworkHandler::groupChange);
   }
}