package com.noone.particleex.command.argument;

import com.google.common.base.Strings;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class Range3ArgumentType implements ArgumentType<Vec3d> {
   public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.range3d.incomplete"));
   private static final DoubleArgumentType PARSER = DoubleArgumentType.doubleArg(0.0D);
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "1 1 1", "1 0 1");

   public static Range3ArgumentType range3() {
      return new Range3ArgumentType();
   }

   public static Vec3d getRange3(CommandContext<ServerCommandSource> context, String name) {
      return context.getArgument(name, Vec3d.class);
   }

   public Vec3d parse(StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      double vx = PARSER.parse(reader);
      if (reader.canRead() && reader.peek() == ' ') {
         reader.skip();
         double vy = PARSER.parse(reader);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            double vz = PARSER.parse(reader);
            return new Vec3d(vx, vy, vz);
         } else {
            reader.setCursor(start);
            throw INCOMPLETE_EXCEPTION.createWithContext(reader);
         }
      } else {
         reader.setCursor(start);
         throw INCOMPLETE_EXCEPTION.createWithContext(reader);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (context.getSource() instanceof CommandSource) {
         String remaining = builder.getRemaining();
         if (Strings.isNullOrEmpty(remaining)) {
            builder.suggest("1");
            builder.suggest("1 1");
            builder.suggest("1 1 1");
         } else {
            Predicate<String> predicate = CommandManager.getCommandValidator(this::parse);
            String[] args = remaining.split(" ");
            if (args.length == 1) {
               if (predicate.test(args[0] + " 1 1")) {
                  builder.suggest(args[0] + " 1");
                  builder.suggest(args[0] + " 1 1");
               }
            } else if (args.length == 2 && predicate.test(String.join(" ", args) + " 1")) {
               builder.suggest(String.join(" ", args) + " 1");
            }
         }

         return builder.buildFuture();
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
