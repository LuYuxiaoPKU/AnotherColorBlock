package com.noone.particleex;

import com.noone.particleex.network.NetworkIdentifiers;
import com.noone.particleex.util.ParticleUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;

/** 客户端 tick 驱动（对应 Fabric 的 ClientTickEvents），仅客户端环境加载。 */
@Mod.EventBusSubscriber(modid = NetworkIdentifiers.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 1.20.1 无 Pre/Post 细分事件，单事件内先后驱动
        ParticleUtil.onStartClientTick();
        ParticleUtil.onEndClientTick();
    }
}
