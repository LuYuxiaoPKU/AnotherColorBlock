package com.noone.particleex.util;

import java.io.PrintStream;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** NeoForge 端错误上报：打印到客户端聊天框。 */
public class ClientMessageUtil implements IMessageSink {

   @Override
   public void addChatMessage(Throwable e) {
      e.printStackTrace(new PrintStream(System.out) {
         public void println(Object x) {
            Minecraft client = Minecraft.getInstance();
            if (client == null) {
               System.out.println(x);
               return;
            }
            client.gui.getChat().addClientSystemMessage(Component.literal(x.toString()));
         }
      });
   }
}