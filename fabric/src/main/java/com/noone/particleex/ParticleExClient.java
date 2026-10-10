package com.noone.particleex;

import com.noone.particleex.network.ClientNetworkHandler;
import com.noone.particleex.network.NetworkIdentifiers;
import com.noone.particleex.network.payload.*;
import com.noone.particleex.util.ParticleUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ParticleExClient implements ClientModInitializer {
   public void onInitializeClient() {
      ClientTickEvents.START_CLIENT_TICK.register(client -> ParticleUtil.onStartClientTick());
      ClientTickEvents.END_CLIENT_TICK.register(client -> ParticleUtil.onEndClientTick());
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.CLEAR_PARTICLE_PACKET_ID, (client, handler, buf, sender) -> { ClearParticlePayload.read(buf); ClientNetworkHandler.clearParticle(); });
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.CLEAR_CACHE_PACKET_ID, (client, handler, buf, sender) -> { ClearCachePayload.read(buf); ClientNetworkHandler.clearCache(); });
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.NORMAL_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.normal(NormalPayload.read(buf)));
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.CONDITIONAL_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.conditional(ConditionalPayload.read(buf)));
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.PARAMETER_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.parameter(ParameterPayload.read(buf)));
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.IMAGE_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.image(ImagePayload.read(buf)));
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.IMAGE_MATRIX_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.imageMatrix(ImageMatrixPayload.read(buf)));
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.VIDEO_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.video(VideoPayload.read(buf)));
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.VIDEO_MATRIX_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.videoMatrix(VideoMatrixPayload.read(buf)));
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.GROUP_REMOVE_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.groupRemove(GroupRemovePayload.read(buf)));
      ClientPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.GROUP_CHANGE_PACKET_ID, (client, handler, buf, sender) -> ClientNetworkHandler.groupChange(GroupChangePayload.read(buf)));
   }
}