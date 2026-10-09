package com.noone.particleex.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class SuggestIntegerArgumentType implements ArgumentType<Integer>, ArgumentTypeInfo.Template<SuggestIntegerArgumentType> {
   private final IntegerArgumentType parser;
   private final int suggest;

   public SuggestIntegerArgumentType(int min, int max, int suggest) {
      this.parser = IntegerArgumentType.integer(min, max);
      this.suggest = suggest;
   }

   public static SuggestIntegerArgumentType integer() {
      return new SuggestIntegerArgumentType(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
   }

   public static SuggestIntegerArgumentType integer(int min) {
      return new SuggestIntegerArgumentType(min, Integer.MAX_VALUE, 0);
   }

   public static SuggestIntegerArgumentType integer(int min, int max) {
      return new SuggestIntegerArgumentType(min, max, 0);
   }

   public static SuggestIntegerArgumentType integer(int min, int max, int suggest) {
      return new SuggestIntegerArgumentType(min, max, suggest);
   }

   public int getMinimum() {
      return this.parser.getMinimum();
   }

   public int getMaximum() {
      return this.parser.getMaximum();
   }

   public int getSuggest() {
      return this.suggest;
   }

   public Integer parse(StringReader reader) throws CommandSyntaxException {
      return this.parser.parse(reader);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (context.getSource() instanceof CommandSource) {
         return String.valueOf(this.suggest).startsWith(builder.getRemaining()) ? builder.suggest(String.valueOf(this.suggest)).buildFuture() : Suggestions.empty();
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return this.parser.getExamples();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof SuggestIntegerArgumentType that)) {
         return false;
      } else {
          return this.parser.equals(that.parser) && this.suggest == that.suggest;
      }
   }

   public int hashCode() {
      return 31 * this.parser.hashCode() + this.suggest;
   }

   public String toString() {
      if (this.parser.getMinimum() == Integer.MIN_VALUE && this.parser.getMaximum() == Integer.MAX_VALUE && this.suggest == 0) {
         return "suggestInteger()";
      } else if (this.parser.getMaximum() == Integer.MAX_VALUE && this.suggest == 0) {
         return "suggestInteger(" + this.parser.getMinimum() + ")";
      } else {
         int min;
         if (this.suggest == 0) {
            min = this.parser.getMinimum();
            return "suggestInteger(" + min + ", " + this.parser.getMaximum() + ")";
         } else {
            min = this.parser.getMinimum();
            return "suggestInteger(" + min + ", " + this.parser.getMaximum() + ", " + this.suggest + ")";
         }
      }
   }

   @Override
   public SuggestIntegerArgumentType instantiate(CommandBuildContext commandRegistryAccess) {
      return this;
   }

   @Override
   public ArgumentTypeInfo<SuggestIntegerArgumentType, ?> type() {
      return Serializer.INSTANCE;
   }

   public static class Serializer implements ArgumentTypeInfo<SuggestIntegerArgumentType, SuggestIntegerArgumentType> {
      public static final SuggestIntegerArgumentType.Serializer INSTANCE = new SuggestIntegerArgumentType.Serializer();
      private Serializer() {}

      @Override
      public void serializeToNetwork(SuggestIntegerArgumentType properties, FriendlyByteBuf buf) {
         buf.writeInt(properties.getMinimum());
         buf.writeInt(properties.getMaximum());
         buf.writeInt(properties.getSuggest());
      }

      @Override
      public SuggestIntegerArgumentType deserializeFromNetwork(FriendlyByteBuf buf) {
         int min = buf.readInt();
         int max = buf.readInt();
         int suggest = buf.readInt();
         return new SuggestIntegerArgumentType(min, max, suggest);
      }

      @Override
      public void serializeToJson(SuggestIntegerArgumentType properties, JsonObject json) {
         json.addProperty("min", properties.getMinimum());
         json.addProperty("max", properties.getMaximum());
         json.addProperty("suggest", properties.getSuggest());
      }

      @Override
      public SuggestIntegerArgumentType unpack(SuggestIntegerArgumentType argumentType) {
         return argumentType;
      }
   }
}
