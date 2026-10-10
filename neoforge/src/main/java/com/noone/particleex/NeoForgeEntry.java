package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.common.Bridge;
import com.noone.particleex.network.ClientNetworkHandler;
import com.noone.particleex.network.NetworkIdentifiers;
import com.noone.particleex.network.payload.ClearCachePayload;
import com.noone.particleex.network.payload.ClearParticlePayload;
import com.noone.particleex.network.payload.ConditionalPayload;
import com.noone.particleex.network.payload.GroupChangePayload;
import com.noone.particleex.network.payload.GroupRemovePayload;
import com.noone.particleex.network.payload.ImageMatrixPayload;
import com.noone.particleex.network.payload.ImagePayload;
import com.noone.particleex.network.payload.NormalPayload;
import com.noone.particleex.network.payload.ParameterPayload;
import com.noone.particleex.network.payload.VideoMatrixPayload;
import com.noone.particleex.network.payload.VideoPayload;
import com.noone.particleex.util.ClientMessageUtil;
import com.noone.particleex.util.MessageBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(NetworkIdentifiers.MOD_ID)
public class NeoForgeEntry {
    /** Forge 47 SimpleChannel（1.20.1 无 CustomPacketPayload 体系，payload 走 ParticlePayload + packetId）。 */
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NetworkIdentifiers.MOD_ID, "main"),
            () -> "1",
            s -> true,
            s -> true);

    public NeoForgeEntry(IEventBus modBus) {
        try {
            ParticleExConfig.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 平台桥注入（发送 + 错误上报）
        Bridge.setSender((world, payload) -> {
            CHANNEL.send(PacketDistributor.DIMENSION.with(() -> world.dimension()), payload);
        });
        MessageBridge.setSink(new ClientMessageUtil());

        registerMessages();
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ParticleExCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    /** 注册 S2C 消息：decoder=read，encoder=write，consumerMainThread 已保证主线程执行。 */
    private void registerMessages() {
        CHANNEL.messageBuilder(ClearParticlePayload.class, 0)
                .decoder(ClearParticlePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly((p) -> ClientNetworkHandler.clearParticle()))
                .add();
        CHANNEL.messageBuilder(ClearCachePayload.class, 1)
                .decoder(ClearCachePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly((p) -> ClientNetworkHandler.clearCache()))
                .add();
        CHANNEL.messageBuilder(NormalPayload.class, 2)
                .decoder(NormalPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::normal))
                .add();
        CHANNEL.messageBuilder(ConditionalPayload.class, 3)
                .decoder(ConditionalPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::conditional))
                .add();
        CHANNEL.messageBuilder(ParameterPayload.class, 4)
                .decoder(ParameterPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::parameter))
                .add();
        CHANNEL.messageBuilder(ImagePayload.class, 5)
                .decoder(ImagePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::image))
                .add();
        CHANNEL.messageBuilder(ImageMatrixPayload.class, 6)
                .decoder(ImageMatrixPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::imageMatrix))
                .add();
        CHANNEL.messageBuilder(VideoPayload.class, 7)
                .decoder(VideoPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::video))
                .add();
        CHANNEL.messageBuilder(VideoMatrixPayload.class, 8)
                .decoder(VideoMatrixPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::videoMatrix))
                .add();
        CHANNEL.messageBuilder(GroupRemovePayload.class, 9)
                .decoder(GroupRemovePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::groupRemove))
                .add();
        CHANNEL.messageBuilder(GroupChangePayload.class, 10)
                .decoder(GroupChangePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(clientOnly(ClientNetworkHandler::groupChange))
                .add();
    }

    /** 包装为仅处理 S2C 方向（本 mod 全部 payload 均为服务端命令下发）。 */
    private static <T> BiConsumer<T, Supplier<NetworkEvent.Context>> clientOnly(Consumer<T> handler) {
        return (payload, ctx) -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                handler.accept(payload);
            }
        };
    }
}
