package com.noone.particleex.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.20.1/1.20.2 的 MappedRegistry.toId 字段类型为 Object2IntMap（1.20.3 起改 Reference2IntMap），
 * 字段名 byId/toId（mojmap 名，运行期 FML remap 到 srg）。其余同 26.2（freeze 不锁集合）。
 */
@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {
   @Accessor("byId")
   ObjectList<Holder.Reference<T>> particleex$getById();

   @Accessor("toId")
   Object2IntMap<T> particleex$getToId();
}
