package com.noone.particleex.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class GroupChangeTypeArgumentType implements ArgumentType<Integer> {
   public static final SimpleCommandExceptionType INVALID_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.group.change.type.invalid"));
   private static final Collection<String> EXAMPLES = Arrays.asList("parameter", "speedexpression");

   public static GroupChangeTypeArgumentType type() {
      return new GroupChangeTypeArgumentType();
   }

   public static int getType(CommandContext<CommandSourceStack> context, String name) {
      return context.getArgument(name, Integer.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (context.getSource() instanceof CommandSource) {
         String remaining = builder.getRemaining();

          for (String example : EXAMPLES) {
              if (example.startsWith(remaining)) {
                  builder.suggest(example);
              }
          }

         return builder.buildFuture();
      } else {
         return Suggestions.empty();
      }
   }

   public Integer parse(StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      String flip = reader.readUnquotedString();
      byte group = -1;
      switch(flip.hashCode()) {
      case 263333407:
         if (flip.equals("speedexpression")) {
            group = 1;
         }
         break;
      case 1954460585:
         if (flip.equals("parameter")) {
            group = 0;
         }
      }

       return switch (group) {
           case 0 -> 0;
           case 1 -> 1;
           default -> {
               reader.setCursor(start);
               throw INVALID_EXCEPTION.createWithContext(reader);
           }
       };
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
