package com.noone.particleex.util;

import java.io.PrintStream;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientMessageUtil {
   private static final Minecraft CLIENT = Minecraft.getInstance();

   public static void addChatMessage(Throwable e) {
      e.printStackTrace(new PrintStream(System.out) {
         public void println(Object x) {
            if (ClientMessageUtil.CLIENT.player != null) {
               ClientMessageUtil.CLIENT.player.sendSystemMessage(Component.literal(x.toString()));
            }
         }
      });
   }
}
