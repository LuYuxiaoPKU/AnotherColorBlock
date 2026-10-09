package com.noone.particleex.util;

import java.io.PrintStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientMessageUtil {
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

   public static void addChatMessage(Throwable e) {
      e.printStackTrace(new PrintStream(System.out) {
         public void println(Object x) {
            ClientMessageUtil.CLIENT.inGameHud.getChatHud().addMessage(Text.literal(x.toString()));
         }
      });
   }
}
