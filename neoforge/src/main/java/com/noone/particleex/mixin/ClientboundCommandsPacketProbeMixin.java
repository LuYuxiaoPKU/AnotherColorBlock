package com.noone.particleex.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 临时探针（1.21.4 commands 包 VarInt too big，排查后移除）：
 * 读侧逐节点打 readerIndex 轨迹 + 读侧结束总量。服务端写侧由 SERIALIZE 探针（SuggestArgumentType）
 * 提供 writerIndex 锚点。对比两侧轨迹定位第一个错位点。
 */
@Mixin(ClientboundCommandsPacket.class)
public class ClientboundCommandsPacketProbeMixin {
   private static final Logger PROBE = LoggerFactory.getLogger("particleex-probe");
   private static int readNodeSeq = 0;

   @Inject(method = {"readNode(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/game/ClientboundCommandsPacket$Entry;"}, at = @At("HEAD"))
   private static void probeReadNodeHead(FriendlyByteBuf buf, CallbackInfoReturnable<Object> cir) {
      PROBE.info("READ node#{} readerIndexAtHead={}", readNodeSeq++, buf.readerIndex());
   }

   @Inject(method = {"<init>(Lnet/minecraft/network/FriendlyByteBuf;)V"}, at = @At("RETURN"))
   private void probeReadEnd(FriendlyByteBuf buf, CallbackInfo ci) {
      PROBE.info("READ side: END readerIndex={} (nodes={})", buf.readerIndex(), readNodeSeq);
   }
}
