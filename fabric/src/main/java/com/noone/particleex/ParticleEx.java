package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.*;
import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.payload.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ParticleEx implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("particleex");
    public static final String MOD_ID = "particleex";

    @Override
    public void onInitialize() {
        try {
            ParticleExConfig.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 平台桥：命令层发送 → Fabric 网络发送；错误上报 → 客户端聊天框
        Bridge.setSender((world, payload) -> {
            net.minecraft.network.FriendlyByteBuf buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
            payload.write(buf);
            ResourceLocation id = payload.packetId();
            PlayerLookup.world(world).forEach(player -> ServerPlayNetworking.send(player, id, buf));
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> ParticleExCommand.register(dispatcher, registryAccess));
        registerArgumentTypes();
    }



    /** 注册自定义命令参数类型（同 26.2 fabric 版；缺注册致客户端进档时命令树序列化崩溃） */
    private static void registerArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "color4"), Color4ArgumentType.class, SingletonArgumentInfo.contextFree(Color4ArgumentType::color4));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "flip"), FlipArgumentType.class, SingletonArgumentInfo.contextFree(FlipArgumentType::flip));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "group_change"), GroupChangeTypeArgumentType.class, SingletonArgumentInfo.contextFree(GroupChangeTypeArgumentType::type));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "range3"), Range3ArgumentType.class, SingletonArgumentInfo.contextFree(Range3ArgumentType::range3));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "rotate"), RotateArgumentType.class, SingletonArgumentInfo.contextFree(RotateArgumentType::rotate));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "speed3"), Speed3ArgumentType.class, SingletonArgumentInfo.contextFree(Speed3ArgumentType::speed3));
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "suggest_string"), SuggestArgumentType.class, SuggestArgumentType.Serializer.INSTANCE);
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "suggest_double"), SuggestDoubleArgumentType.class, SuggestDoubleArgumentType.Serializer.INSTANCE);
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(MOD_ID, "suggest_integer"), SuggestIntegerArgumentType.class, SuggestIntegerArgumentType.Serializer.INSTANCE);
    }

}
