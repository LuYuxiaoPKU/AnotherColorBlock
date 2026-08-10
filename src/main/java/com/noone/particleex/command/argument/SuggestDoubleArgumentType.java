package com.noone.particleex.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class SuggestDoubleArgumentType implements ArgumentType<Double>, ArgumentSerializer.ArgumentTypeProperties<SuggestDoubleArgumentType>{
   private final DoubleArgumentType parser;
   private final double suggest;

   public SuggestDoubleArgumentType(double min, double max, double suggest) {
      this.parser = DoubleArgumentType.doubleArg(min, max);
      this.suggest = suggest;
   }

   public static SuggestDoubleArgumentType doubleArg() {
      return new SuggestDoubleArgumentType(Double.MIN_VALUE, Double.MAX_VALUE, 0.0D);
   }

   public static SuggestDoubleArgumentType doubleArg(double min) {
      return new SuggestDoubleArgumentType(min, Double.MAX_VALUE, 0.0D);
   }

   public static SuggestDoubleArgumentType doubleArg(double min, double max) {
      return new SuggestDoubleArgumentType(min, max, 0.0D);
   }

   public static SuggestDoubleArgumentType doubleArg(double min, double max, double suggest) {
      return new SuggestDoubleArgumentType(min, max, suggest);
   }

   public double getMinimum() {
      return this.parser.getMinimum();
   }

   public double getMaximum() {
      return this.parser.getMaximum();
   }

   public double getSuggest() {
      return this.suggest;
   }

   public Double parse(StringReader reader) throws CommandSyntaxException {
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
      } else if (!(o instanceof SuggestDoubleArgumentType that)) {
         return false;
      } else {
          return this.parser.equals(that.parser) && this.suggest == that.suggest;
      }
   }

   public int hashCode() {
      return (int)((double)(31 * this.parser.hashCode()) + this.suggest);
   }

   public String toString() {
      if (this.parser.getMinimum() == Double.MIN_VALUE && this.parser.getMaximum() == Double.MAX_VALUE && this.suggest == 0.0D) {
         return "suggestDouble()";
      } else if (this.parser.getMaximum() == Double.MAX_VALUE && this.suggest == 0.0D) {
         return "suggestDouble(" + this.parser.getMinimum() + ")";
      } else {
         double min;
         if (this.suggest == 0.0D) {
            min = this.parser.getMinimum();
            return "suggestDouble(" + min + ", " + this.parser.getMaximum() + ")";
         } else {
            min = this.parser.getMinimum();
            return "suggestDouble(" + min + ", " + this.parser.getMaximum() + ", " + this.suggest + ")";
         }
      }
   }


   @Override
   public SuggestDoubleArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
      return this;
   }

   @Override
   public ArgumentSerializer<SuggestDoubleArgumentType, ?> getSerializer() {
      return Serializer.INSTANCE;
   }

   public static class Serializer implements ArgumentSerializer<SuggestDoubleArgumentType, SuggestDoubleArgumentType> {
      public static final Serializer INSTANCE = new Serializer();
      private Serializer() {}

      @Override
      public void writePacket(SuggestDoubleArgumentType properties, PacketByteBuf buf) {
         buf.writeDouble(properties.getMinimum());
         buf.writeDouble(properties.getMaximum());
         buf.writeDouble(properties.getSuggest());
      }

      @Override
      public SuggestDoubleArgumentType fromPacket(PacketByteBuf buf) {
         double min = buf.readDouble();
         double max = buf.readDouble();
         double suggest = buf.readDouble();
         return new SuggestDoubleArgumentType(min, max, suggest);
      }

      @Override
      public void writeJson(SuggestDoubleArgumentType properties, JsonObject json) {
         json.addProperty("min", properties.getMinimum());
         json.addProperty("max", properties.getMaximum());
         json.addProperty("suggest", properties.getSuggest());
      }

      @Override
      public SuggestDoubleArgumentType getArgumentTypeProperties(SuggestDoubleArgumentType argumentType) {
         return argumentType;
      }
   }
}
