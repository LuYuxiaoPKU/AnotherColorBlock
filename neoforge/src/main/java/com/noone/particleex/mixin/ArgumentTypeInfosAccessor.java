package com.noone.particleex.mixin;

import java.util.Map;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 26.x 无公开的参数类型注册 API（ArgumentTypeInfos 只剩 bootstrap/byClass/unpack），
 * 且 BuiltInRegistries.COMMAND_ARGUMENT_TYPE 在 neoforge mod 构造期已冻结。
 * 与 Fabric API 的 ArgumentTypeInfosAccessor 同款思路：直取私有 BY_CLASS 映射。
 * 字节码实证（26.2 ClientboundCommandsPacket.createEntry）：
 * 序列化只走 ArgumentTypeInfos.unpack → BY_CLASS → Template，不查注册表 id，
 * 因此仅需塞 BY_CLASS，无需（也不能）注册 BuiltInRegistries。
 */
@Mixin(ArgumentTypeInfos.class)
public interface ArgumentTypeInfosAccessor {
   @Accessor("BY_CLASS")
   static Map<Class<?>, ArgumentTypeInfo<?, ?>> particleex$getByClass() {
      throw new AssertionError();
   }
}