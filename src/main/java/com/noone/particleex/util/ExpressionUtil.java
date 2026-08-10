package com.noone.particleex.util;

import com.google.common.base.Strings;

public class ExpressionUtil {
   public static IExecutable parse(String expression) {
      if (!Strings.isNullOrEmpty(expression) && !expression.equals("null")) {
         try {
            return ClassExpression.parse(expression);
         } catch (RuntimeException var2) {
            ClientMessageUtil.addChatMessage(var2);
            throw var2;
         }
      } else {
         return null;
      }
   }
}
