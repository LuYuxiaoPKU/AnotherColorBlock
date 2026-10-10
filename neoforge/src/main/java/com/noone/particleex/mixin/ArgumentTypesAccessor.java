package com.noone.particleex.mixin;

import java.util.Map;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.20.5/1.20.6 的同步类名为 ArgumentTypes（复数，1.21 起改名 ArgumentTypeInfos）。
 * 直取私有 BY_CLASS；配合注册表直塞完成参数类型注册（与 26.2 修复同构）。
 */
@Mixin(ArgumentTypes.class)
public interface ArgumentTypesAccessor {
   @Accessor("BY_CLASS")
   static Map<Class<?>, ArgumentTypeInfo<?, ?>> particleex$getByClass() {
      throw new AssertionError();
   }
}
