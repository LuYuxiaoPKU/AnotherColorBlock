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
import com.noone.particleex.mixin.MappedRegistryAccessor;
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


import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;


import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;


import net.neoforged.fml.common.Mod;


import net.neoforged.neoforge.common.NeoForge;


import net.neoforged.neoforge.event.RegisterCommandsEvent;


import net.neoforged.neoforge.network.NetworkEvent;


import net.neoforged.neoforge.network.NetworkRegistry;


import net.neoforged.neoforge.network.PacketDistributor;


import net.neoforged.neoforge.network.PlayNetworkDirection;


import net.neoforged.neoforge.network.simple.MessageFunctions.MessageConsumer;

import net.neoforged.neoforge.network.simple.SimpleChannel;





import java.io.IOException;


import java.util.function.BiConsumer;


import java.util.function.Consumer;





@Mod(NetworkIdentifiers.MOD_ID)


public class NeoForgeEntry {


    /** 20.2 无 RegisterPayloadHandlerEvent / IPayloadContext（1.20.5+ API），用 SimpleChannel 老式通道（NeoForge 1.20.2 MDK 同款）。 */


    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder


            .named(new ResourceLocation(NetworkIdentifiers.MOD_ID, "main"))


            .clientAcceptedVersions(a -> true)


            .serverAcceptedVersions(a -> true)


            .networkProtocolVersion(() -> "1")


            .simpleChannel();





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


        registerArgumentTypes();
        NeoForge.EVENT_BUS.addListener(this::registerCommands);


    }





    /**
     * 注册自定义命令参数类型（1.20.1~1.20.4 同步类名 ArgumentTypeInfos，同 26.2 修复：
     * BY_CLASS + 注册表直塞；MappedRegistry.toId 类型按版本取 Object2IntMap/Reference2IntMap）。
     */
    private void registerArgumentTypes() {
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

        Registry<ArgumentTypeInfo<?, ?>> registry = BuiltInRegistries.COMMAND_ARGUMENT_TYPE;
        if (!(registry instanceof MappedRegistry)) {
            throw new IllegalStateException("COMMAND_ARGUMENT_TYPE 不是 MappedRegistry，无法注册参数类型");
        }
        @SuppressWarnings({"rawtypes", "unchecked"})
        MappedRegistryAccessor regAccessor = (MappedRegistryAccessor) (Object) registry;
        ObjectList<Holder.Reference<Object>> byId = (ObjectList) regAccessor.particleex$getById();
        Object2IntMap<Object> toId = (Object2IntMap) regAccessor.particleex$getToId();
        for (ArgumentTypeInfo<?, ?> info : byClass.values()) {
            int id = byId.size();
            byId.add((Holder.Reference<Object>) (Object) Holder.Reference.createIntrusive((HolderOwner<Object>) (Object) registry, info));
            toId.put(info, id);
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {


        ParticleExCommand.register(event.getDispatcher(), event.getBuildContext());


    }





    /** 注册 S2C 消息：decoder=read，encoder=write，consumerMainThread 已保证主线程执行（对比 1.20.5+ 的 enqueueWork）。 */


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


    private static <T> MessageConsumer<T> clientOnly(Consumer<T> handler) {


        return (payload, ctx) -> {


            if (ctx.getDirection() == PlayNetworkDirection.PLAY_TO_CLIENT) {


                handler.accept(payload);


            }


        };


    }


}


