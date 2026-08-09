package com.noone.particleex.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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

public class SuggestArgumentType implements ArgumentType<String>, ArgumentSerializer.ArgumentTypeProperties<SuggestArgumentType>{
   private final StringArgumentType parser;
   private final String[] suggests;

   public SuggestArgumentType(StringArgumentType parser, String[] suggests) {
      this.parser = parser;
      this.suggests = suggests;
   }

    public static SuggestArgumentType argument(StringArgumentType parser, String[] suggests) {
      return new SuggestArgumentType(parser, suggests);
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      return this.parser.parse(reader);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (context.getSource() instanceof CommandSource) {
         String remaining = builder.getRemaining();
          for (String suggest : this.suggests) {
              if (suggest.startsWith(remaining)) {
                  builder.suggest(suggest);
              }
          }
         return builder.buildFuture();
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
       return this.parser.getExamples();
   }

    @Override
    public SuggestArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this;
    }

    @Override
    public ArgumentSerializer<SuggestArgumentType, ?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements ArgumentSerializer<SuggestArgumentType, SuggestArgumentType> {
        public static final SuggestArgumentType.Serializer INSTANCE = new SuggestArgumentType.Serializer();
        private Serializer() {}

        @Override
        public void writePacket(SuggestArgumentType properties, PacketByteBuf buf) {
            buf.writeInt(properties.suggests.length);
            for (String suggest : properties.suggests) {
                buf.writeString(suggest);
            }
        }

        @Override
        public SuggestArgumentType fromPacket(PacketByteBuf buf) {
            int length = buf.readInt();
            String[] suggests = new String[length];
            for (int i = 0; i < length; i++) {
                suggests[i] = buf.readString(32767);
            }
            return new SuggestArgumentType(StringArgumentType.string(), suggests);
        }

        @Override
        public void writeJson(SuggestArgumentType properties, JsonObject json) {
            JsonObject suggestsJson = new JsonObject();
            for (String suggest : properties.suggests) {
                suggestsJson.addProperty(suggest, suggest);
            }
            json.add("suggestions", suggestsJson);
        }

        @Override
        public SuggestArgumentType getArgumentTypeProperties(SuggestArgumentType argumentType) {
            return argumentType;
        }
    }
}
