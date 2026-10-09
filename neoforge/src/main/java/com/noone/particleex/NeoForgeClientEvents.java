package com.noone.particleex;

import com.noone.particleex.network.NetworkIdentifiers;
import com.noone.particleex.util.ParticleUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** 客户端 tick 驱动（对应 Fabric 的 ClientTickEvents），仅客户端环境加载。 */
@EventBusSubscriber(modid = NetworkIdentifiers.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {
    @SubscribeEvent
    public static void onPreTick(ClientTickEvent.Pre event) {
        ParticleUtil.onStartClientTick();
    }

    @SubscribeEvent
    public static void onPostTick(ClientTickEvent.Post event) {
        ParticleUtil.onEndClientTick();
    }
}
