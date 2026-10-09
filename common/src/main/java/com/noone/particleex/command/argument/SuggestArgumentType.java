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

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class SuggestArgumentType implements ArgumentType<String>, ArgumentTypeInfo.Template<SuggestArgumentType>{
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
    public SuggestArgumentType instantiate(CommandBuildContext commandRegistryAccess) {
        return this;
    }

    @Override
    public ArgumentTypeInfo<SuggestArgumentType, ?> type() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements ArgumentTypeInfo<SuggestArgumentType, SuggestArgumentType> {
        public static final SuggestArgumentType.Serializer INSTANCE = new SuggestArgumentType.Serializer();
        private Serializer() {}

        @Override
        public void serializeToNetwork(SuggestArgumentType properties, FriendlyByteBuf buf) {
            buf.writeInt(properties.suggests.length);
            for (String suggest : properties.suggests) {
                buf.writeUtf(suggest);
            }
        }

        @Override
        public SuggestArgumentType deserializeFromNetwork(FriendlyByteBuf buf) {
            int length = buf.readInt();
            String[] suggests = new String[length];
            for (int i = 0; i < length; i++) {
                suggests[i] = buf.readUtf(32767);
            }
            return new SuggestArgumentType(StringArgumentType.string(), suggests);
        }

        @Override
        public void serializeToJson(SuggestArgumentType properties, JsonObject json) {
            JsonObject suggestsJson = new JsonObject();
            for (String suggest : properties.suggests) {
                suggestsJson.addProperty(suggest, suggest);
            }
            json.add("suggestions", suggestsJson);
        }

        @Override
        public SuggestArgumentType unpack(SuggestArgumentType argumentType) {
            return argumentType;
        }
    }
}
