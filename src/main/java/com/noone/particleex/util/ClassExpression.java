package com.noone.particleex.util;

import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ClassExpression implements IExecutable {
   private static final Map<String, Method> CACHE = Maps.newHashMap();
   private static int index = 0;
   private ParticleStruct struct = new ParticleStruct();
   private Method method;

   private ClassExpression(String expression) {
      Lexer lexer = new Lexer(expression);
      Expression[] exps = Parser.parseBlock(lexer);
      CodeGen codeGen = new CodeGen(exps);
      int var10001 = index++;
      Class<?> clazz = codeGen.codeGenBlock("EXP_" + var10001);

      try {
         this.method = clazz.getMethod("invoke", ParticleStruct.class);
      } catch (SecurityException | NoSuchMethodException var7) {
         throw new RuntimeException(var7);
      }
   }

   private ClassExpression(Method method) {
      this.method = method;
   }

   public static ClassExpression parse(String expression) {
      if (CACHE.containsKey(expression)) {
         return new ClassExpression(CACHE.get(expression));
      } else {
         ClassExpression instance = new ClassExpression(expression);
         CACHE.put(expression, instance.method);
         return instance;
      }
   }

   public ParticleStruct getData() {
      return this.struct;
   }

   public int invoke() {
      try {
         return (Integer)this.method.invoke(null, this.struct);
      } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
         ClientMessageUtil.addChatMessage(var2);
         throw new RuntimeException(var2);
      }
   }
}
