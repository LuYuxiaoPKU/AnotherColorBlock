package com.noone.particleex.mixin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 直取 MappedRegistry 的 byId/toId 字段。
 * 用途：BuiltInRegistries.COMMAND_ARGUMENT_TYPE 在 neoforge mod 构造期已冻结
 * （Registry.register 抛 IllegalStateException），但发包协议（ClientboundCommandsPacket）
 * 写侧 serializeCap 读 getId(info)、读侧 byId(id)，必须让注册表认识自定义参数类型 info。
 * 字节码实证（26.2）：MappedRegistry.freeze() 只置 frozen 标志 + 绑定 holder，
 * 不包装 byId/toId 集合——freeze 后字段级插入仍然可行，等价于 fabric 在未冻结时
 * 走公开 Registry.register 的效果（byId.add + toId.put，id 取列表当前大小）。
 */
@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {
   @Accessor("byId")
   ObjectList<Holder.Reference<T>> particleex$getById();

   @Accessor("toId")
   Reference2IntMap<T> particleex$getToId();
}