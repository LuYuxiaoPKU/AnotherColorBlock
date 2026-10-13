package com.noone.particleex.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

/**
 * 临时探针（1.21.4 commands 包 VarInt too big，排查后移除）：
 * 读侧 readNode RETURN 时反射提取节点名 + argumentType 类型，形成读侧节点序列；
 * 与服务端写侧（createEntry 打点）zip 对比定位第一个错位节点。
 */
@Mixin(ClientboundCommandsPacket.class)
public class ClientboundCommandsPacketProbeMixin {
   private static final Logger PROBE = LoggerFactory.getLogger("particleex-probe");
   private static int readNodeSeq = 0;
   private static Field entryStubField;
   private static Field argIdField;
   private static Field argTemplateField;

   @Inject(method = {"readNode(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/game/ClientboundCommandsPacket$Entry;"}, at = @At("RETURN"))
   private static void probeReadNodeReturn(FriendlyByteBuf buf, CallbackInfoReturnable<Object> cir) {
      try {
         if (entryStubField == null) {
            Class<?> entryCls = Class.forName("net.minecraft.network.protocol.game.ClientboundCommandsPacket$Entry");
            entryStubField = entryCls.getDeclaredField("stub");
            entryStubField.setAccessible(true);
            Class<?> argCls = Class.forName("net.minecraft.network.protocol.game.ClientboundCommandsPacket$ArgumentNodeStub");
            argIdField = argCls.getDeclaredField("id");
            argIdField.setAccessible(true);
            argTemplateField = argCls.getDeclaredField("argumentType");
            argTemplateField.setAccessible(true);
         }
         Object entry = cir.getReturnValue();
         Object stub = entry == null ? null : entryStubField.get(entry);
         if (stub == null) {
            PROBE.info("READ#{} readerIndex={} stub=NULL", readNodeSeq++, buf.readerIndex());
         } else {
            String cls = stub.getClass().getSimpleName();
            if (cls.contains("Argument")) {
               String name = (String) argIdField.get(stub);
               Object tmpl = argTemplateField.get(stub);
               PROBE.info("READ#{} readerIndex={} ARG name={} templateType={}", readNodeSeq++, buf.readerIndex(), name,
                  tmpl == null ? "null" : tmpl.getClass().getSimpleName());
            } else {
               PROBE.info("READ#{} readerIndex={} {}", readNodeSeq++, buf.readerIndex(), cls);
            }
         }
      } catch (Throwable t) {
         PROBE.info("READ#{} readerIndex={} probe-error={}", readNodeSeq++, buf.readerIndex(), t);
      }
   }
}
