package com.noone.particleex;

import com.noone.particleex.network.NetworkIdentifiers;
import com.noone.particleex.util.ParticleUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

/** 客户端 tick 驱动（对应 Fabric 的 ClientTickEvents），仅客户端环境加载。 */
@Mod.EventBusSubscriber(modid = NetworkIdentifiers.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 1.20.4 无 Pre/Post 细分事件（20.6+ 才有），单事件内先后驱动
        ParticleUtil.onStartClientTick();
        ParticleUtil.onEndClientTick();
    }
}
