package com.noone.particleex;

import com.noone.particleex.command.ParticleExCommand;
import com.noone.particleex.command.argument.Color4ArgumentType;
import com.noone.particleex.command.argument.FlipArgumentType;
import com.noone.particleex.command.argument.GroupChangeTypeArgumentType;
import com.noone.particleex.command.argument.Range3ArgumentType;
import com.noone.particleex.command.argument.RotateArgumentType;
import com.noone.particleex.command.argument.Speed3ArgumentType;
import com.noone.particleex.command.argument.SuggestArgumentType;
import com.noone.particleex.command.argument.SuggestDoubleArgumentType;
import com.noone.particleex.command.argument.SuggestIntegerArgumentType;
import com.noone.particleex.mixin.ArgumentTypeInfosAccessor;
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
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
        if (FMLEnvironment.dist.isClient()) {
            MessageBridge.setSink(new ClientMessageUtil());
        }

        registerMessages();
        registerArgumentTypes(modBus);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    /**
     * 注册自定义命令参数类型（1.20.1~1.20.4 同步类名 ArgumentTypeInfos；
     * BY_CLASS put 仍在构造期，注册表写入走 RegisterEvent）。
     */
    private void registerArgumentTypes(IEventBus modBus) {
        Map<Class<?>, ArgumentTypeInfo<?, ?>> byClass = ArgumentTypeInfosAccessor.particleex$getByClass();
        byClass.put(Color4ArgumentType.class, SingletonArgumentInfo.contextFree(Color4ArgumentType::color4));
        byClass.put(FlipArgumentType.class, SingletonArgumentInfo.contextFree(FlipArgumentType::flip));
        byClass.put(GroupChangeTypeArgumentType.class, SingletonArgumentInfo.contextFree(GroupChangeTypeArgumentType::type));
        byClass.put(Range3ArgumentType.class, SingletonArgumentInfo.contextFree(Range3ArgumentType::range3));
        byClass.put(RotateArgumentType.class, SingletonArgumentInfo.contextFree(RotateArgumentType::rotate));
        byClass.put(Speed3ArgumentType.class, SingletonArgumentInfo.contextFree(Speed3ArgumentType::speed3));
        byClass.put(SuggestArgumentType.class, SuggestArgumentType.Serializer.INSTANCE);
        byClass.put(SuggestDoubleArgumentType.class, SuggestDoubleArgumentType.Serializer.INSTANCE);
        byClass.put(SuggestIntegerArgumentType.class, SuggestIntegerArgumentType.Serializer.INSTANCE);

        // RegisterEvent（mod bus）注册（同 1.20.2/1.20.4/1.20.6/1.21.x 模式）：
        // 1.20.1 构造期字段直塞 createIntrusive(registry,...) 预判同款 ClassCastException
        // （Forge 47 MappedRegistry 非自身 HolderOwner）且 RegistrySnapshot 丢条目。
        String[] names = {"color4", "flip", "group_change_type", "range3", "rotate", "speed3",
            "suggest", "suggest_double", "suggest_integer"};
        // Forge 47 的 EventBus 无 addListener(Class, Consumer) 双参重载（1.21+ 才有），用泛型 Consumer 注册
        modBus.addListener((Consumer<RegisterEvent>) event -> {
            if (!BuiltInRegistries.COMMAND_ARGUMENT_TYPE.key().equals(event.getRegistryKey())) {
                return;
            }
            List<ArgumentTypeInfo<?, ?>> own = List.of(
                byClass.get(Color4ArgumentType.class),
                byClass.get(FlipArgumentType.class),
                byClass.get(GroupChangeTypeArgumentType.class),
                byClass.get(Range3ArgumentType.class),
                byClass.get(RotateArgumentType.class),
                byClass.get(Speed3ArgumentType.class),
                byClass.get(SuggestArgumentType.class),
                byClass.get(SuggestDoubleArgumentType.class),
                byClass.get(SuggestIntegerArgumentType.class)
            );
            for (int i = 0; i < names.length; i++) {
                if (own.get(i) == null) {
                    throw new IllegalStateException("自定义参数类型 info 为 null，BY_CLASS put 未生效: " + names[i]);
                }
                //noinspection unchecked,rawtypes
                Registry.register(event.getVanillaRegistry(), new ResourceLocation(NetworkIdentifiers.MOD_ID, names[i]), own.get(i));
            }
        });
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ParticleExCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    /** 注册 S2C 消息：decoder=read，encoder=write，consumerMainThread 已保证主线程执行。 */
    private void registerMessages() {
        CHANNEL.messageBuilder(ClearParticlePayload.class, 0)
                .decoder(ClearParticlePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ClearParticlePayload>clientOnly((p) -> ClientNetworkHandler.clearParticle()))
                .add();
        CHANNEL.messageBuilder(ClearCachePayload.class, 1)
                .decoder(ClearCachePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ClearCachePayload>clientOnly((p) -> ClientNetworkHandler.clearCache()))
                .add();
        CHANNEL.messageBuilder(NormalPayload.class, 2)
                .decoder(NormalPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<NormalPayload>clientOnly(ClientNetworkHandler::normal))
                .add();
        CHANNEL.messageBuilder(ConditionalPayload.class, 3)
                .decoder(ConditionalPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ConditionalPayload>clientOnly(ClientNetworkHandler::conditional))
                .add();
        CHANNEL.messageBuilder(ParameterPayload.class, 4)
                .decoder(ParameterPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ParameterPayload>clientOnly(ClientNetworkHandler::parameter))
                .add();
        CHANNEL.messageBuilder(ImagePayload.class, 5)
                .decoder(ImagePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ImagePayload>clientOnly(ClientNetworkHandler::image))
                .add();
        CHANNEL.messageBuilder(ImageMatrixPayload.class, 6)
                .decoder(ImageMatrixPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<ImageMatrixPayload>clientOnly(ClientNetworkHandler::imageMatrix))
                .add();
        CHANNEL.messageBuilder(VideoPayload.class, 7)
                .decoder(VideoPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<VideoPayload>clientOnly(ClientNetworkHandler::video))
                .add();
        CHANNEL.messageBuilder(VideoMatrixPayload.class, 8)
                .decoder(VideoMatrixPayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<VideoMatrixPayload>clientOnly(ClientNetworkHandler::videoMatrix))
                .add();
        CHANNEL.messageBuilder(GroupRemovePayload.class, 9)
                .decoder(GroupRemovePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<GroupRemovePayload>clientOnly(ClientNetworkHandler::groupRemove))
                .add();
        CHANNEL.messageBuilder(GroupChangePayload.class, 10)
                .decoder(GroupChangePayload::read)
                .encoder((p, buf) -> p.write(buf))
                .consumerMainThread(this.<GroupChangePayload>clientOnly(ClientNetworkHandler::groupChange))
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
