package com.noone.particleex;

import com.noone.particleex.network.payload.*;
import com.noone.particleex.network.ClientNetworkHandler;
import com.noone.particleex.util.ParticleUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ParticleExClient implements ClientModInitializer {
   public void onInitializeClient() {
      ClientTickEvents.START_CLIENT_TICK.register(client -> ParticleUtil.onStartClientTick());
      ClientTickEvents.END_CLIENT_TICK.register(client -> ParticleUtil.onEndClientTick());
      PayloadTypeRegistry.clientboundPlay().register(ClearParticlePayload.ID, ClearParticlePayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(ClearCachePayload.ID, ClearCachePayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(NormalPayload.ID, NormalPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(ConditionalPayload.ID, ConditionalPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(ParameterPayload.ID, ParameterPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(ImagePayload.ID, ImagePayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(ImageMatrixPayload.ID, ImageMatrixPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(VideoPayload.ID, VideoPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(VideoMatrixPayload.ID, VideoMatrixPayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(GroupRemovePayload.ID,GroupRemovePayload.CODEC);
      PayloadTypeRegistry.clientboundPlay().register(GroupChangePayload.ID,GroupChangePayload.CODEC);
      ClientPlayNetworking.registerGlobalReceiver(ClearParticlePayload.ID, (payload, context) -> ClientNetworkHandler.clearParticle(context));
      ClientPlayNetworking.registerGlobalReceiver(ClearCachePayload.ID, (payload, context) -> ClientNetworkHandler.clearCache(context));
      ClientPlayNetworking.registerGlobalReceiver(NormalPayload.ID, ClientNetworkHandler::normal);
      ClientPlayNetworking.registerGlobalReceiver(ConditionalPayload.ID, ClientNetworkHandler::conditional);
      ClientPlayNetworking.registerGlobalReceiver(ParameterPayload.ID, ClientNetworkHandler::parameter);
      ClientPlayNetworking.registerGlobalReceiver(ImagePayload.ID,ClientNetworkHandler::image);
      ClientPlayNetworking.registerGlobalReceiver(ImageMatrixPayload.ID, ClientNetworkHandler::imageMatrix);
      ClientPlayNetworking.registerGlobalReceiver(VideoPayload.ID, ClientNetworkHandler::video);
      ClientPlayNetworking.registerGlobalReceiver(VideoMatrixPayload.ID, ClientNetworkHandler::videoMatrix);
      ClientPlayNetworking.registerGlobalReceiver(GroupRemovePayload.ID, ClientNetworkHandler::groupRemove);
      ClientPlayNetworking.registerGlobalReceiver(GroupChangePayload.ID, ClientNetworkHandler::groupChange);
   }
}
