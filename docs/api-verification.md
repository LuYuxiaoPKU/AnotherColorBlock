# API 核验记录（Mojang 官方映射 · 1.21.11）

本文件记录通过**字节码/反汇编级核验**确认的 Minecraft API 事实，作为跨版本迁移的权威依据。
与 [multiloader-migration.md](multiloader-migration.md) 的映射表互为补充：映射表解决"yarn 叫什么、mojang 叫什么"，
本表解决"这个类/方法在 1.21.11 里**真实存在且签名如何**"——仅凭映射表无法回答的问题（类被移除、字段改名、注入点变化）。

## 核验方法（可复现）

```bash
# 本地 loom 缓存中的 named jar（官方映射反编译产物）
JAR="$HOME/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-clientonly/\
1.21.11-loom.mappings.1_21_11.layered+hash.2198-v2/minecraft-clientonly-*.jar"
unzip -p "$JAR" "<类路径>.class" > /tmp/x.class
javap -p /tmp/x.class          # 方法与字段签名
javap -c /tmp/x.class          # 字节码（<init> 内容、字段初始化）
```

注意：本机 Windows 的 `javap`/`java` 无法解析 MSYS 的 `/tmp` 路径，须用 `$TEMP` 或项目内绝对路径。

## 已核验事实（2026-10-09，commit 7a5db9a 前后）

### net.minecraft.client.particle.Particle
| 成员 | 签名 | 说明 |
|---|---|---|
| `x/y/z/xd/yd/zd` | `protected double` | @Shadow 字段 ✓ |
| `tick()` / `move(d,d,d)` / `setPos(d,d,d)` | `public void` | ✓ |
| `isAlive()` | `public boolean` | ✓ |
| `remove()` | `public void` | **1.21.11 无 `markDead()`**，1.20.5+ 已改名 remove |
| `setLifetime(int)` | `public void` | yarn setMaxAge 对应 |
| `getParticleLimit()` | `public Optional<ParticleLimit>` | ParticleManagerMixin removeIf 里使用 |

### net.minecraft.client.particle.SingleQuadParticle
- `protected float alpha` ✓（@Accessor 目标）
- `setColor(float, float, float)` ✓（ParticleMixin.syncRenderColor 使用）

### net.minecraft.client.particle.ParticleEngine
- `createParticle(ParticleOptions, double×6)` → Particle ✓
- `setLevel(ClientLevel)` ✓（**不是 setWorld**）
- `updateCount(ParticleLimit, int)` `protected` ✓（ParticleManagerAccessor @Invoker 目标）

### net.minecraft.client.particle.ParticleGroup\<P extends Particle\>
- `tickParticles()` public / `tickParticle(Particle)` private ✓（注入点）
- 字段：`protected final ParticleEngine engine`、`protected final Queue<P> particles`
- **`<init>` 中是 `EvictingQueue.create(16384)`（Guava 静态方法），不是 `new ArrayDeque<>(N)`**
  - 容量 ModifyArg 的 target 必须是 `com/google/common/collect/EvictingQueue.create:(I)Lcom/google/common/collect/EvictingQueue;`（remap=false）
  - 这是字节码级发现：yarn 与 mojang 的映射表不会暴露这一点，只改类名会导致 mixin 注入在运行时找不到目标而崩溃

### net.minecraft.client.Minecraft
- `public ClientLevel level` 字段 ✓（**不是 world**）

### net.minecraft.commands.synchronization
- **1.21.11 不存在 `ConstantArgumentTypes`**（旧版本用），替代品为 `SingletonArgumentInfo`：
  - `contextFree(Supplier<T>)` / `contextAware(Function<CommandBuildContext, T>)` → `SingletonArgumentInfo<T>`（implements ArgumentTypeInfo）
  - fabric-api 的 `ArgumentTypeRegistry.registerArgumentType(ResourceLocation, Class, ArgumentTypeInfo)` 即用 `SingletonArgumentInfo.contextFree(...)` 注册常量参数类型

## 教训与规则

1. **映射表改名 ≠ 全部**：类/方法级改名（yarn→mojang）用 tiny 能查；**签名级变革（方法移除、注入点实现变化）只能靠字节码核验**。每次做新版本时对全部 mixin 注入点和 @Accessor/@Invoker/@Shadow 目标执行一遍本表流程。
2. **借用 yarn 旧版实现时**：yarn 版能工作不代表迁移后目标存在——本次 1.21.11 的 `ArrayDeque.<init>` ModifyArg 与 `markDead()` 即为 yarn 旧分支残留（可见的实现差异说明目标版本经历了重构）。
3. 核验时优先用**本项目锁定的 MC 版本的 named jar**，不要凭记忆或别的版本结论。

## 待核验项（下一个版本分支时执行）
- 新版本 ParticleGroup `<init>` 是否仍用 EvictingQueue（或恢复 ArrayDeque）
- Particle.remove() 是否保留（1.20 分支为 markDead）
- SingletonArgumentInfo 是否仍是 constant 参数的注册路径（26.x 可能有新结构）