package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.*;
import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.payload.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
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
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> ParticleExCommand.register(dispatcher));
        registerArgumentTypes();
    }



    /** 注册自定义命令参数类型（1.18.2 旧体系：vanilla ArgumentTypes.register + EmptyArgumentSerializer） */
    private static void registerArgumentTypes() {
        ArgumentTypes.register(MOD_ID + ":color4", Color4ArgumentType.class, new EmptyArgumentSerializer<>(Color4ArgumentType::color4));
        ArgumentTypes.register(MOD_ID + ":flip", FlipArgumentType.class, new EmptyArgumentSerializer<>(FlipArgumentType::flip));
        ArgumentTypes.register(MOD_ID + ":group_change", GroupChangeTypeArgumentType.class, new EmptyArgumentSerializer<>(GroupChangeTypeArgumentType::type));
        ArgumentTypes.register(MOD_ID + ":range3", Range3ArgumentType.class, new EmptyArgumentSerializer<>(Range3ArgumentType::range3));
        ArgumentTypes.register(MOD_ID + ":rotate", RotateArgumentType.class, new EmptyArgumentSerializer<>(RotateArgumentType::rotate));
        ArgumentTypes.register(MOD_ID + ":speed3", Speed3ArgumentType.class, new EmptyArgumentSerializer<>(Speed3ArgumentType::speed3));
        ArgumentTypes.register(MOD_ID + ":suggest_string", SuggestArgumentType.class, SuggestArgumentType.Serializer.INSTANCE);
        ArgumentTypes.register(MOD_ID + ":suggest_double", SuggestDoubleArgumentType.class, SuggestDoubleArgumentType.Serializer.INSTANCE);
        ArgumentTypes.register(MOD_ID + ":suggest_integer", SuggestIntegerArgumentType.class, SuggestIntegerArgumentType.Serializer.INSTANCE);
    }

}
