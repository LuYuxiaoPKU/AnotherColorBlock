# AnotherColorBlock 多平台（Fabric + NeoForge）改造设计文档

> 目标：将本项目（AnotherColorBlock，社区维护版）从"单一 Fabric 模块 + 版本分支"改造成
> "common / fabric / neoforge 三模块 multiloader 结构"，并固化到 1.21 ~ 26.2 的全部版本分支
> （1.21, 1.21.2, 1.21.4, 1.21.5, 1.21.6, 1.21.9, 1.21.11, 26.1, 26.2）。

## 1. 背景与决策（2026-10-09 确认）

- 平台：Fabric + NeoForge（手写 multiloader，不引入 Architectury）
- 版本：沿用上游"每版本一分支"策略；本分支（1.21.11）先落地为模板
- 统计：`.claude/` hooks 独立统计 token 用量，提交/推送自动更新 README「开发统计」节

## 2. 代码复用评估（耦合分析结论）

- **63% 文件 / 68% 行**可直接进 common；抽象 4 个接口后实际可复用 **80–85%**
- 进 common 的资产：表达式引擎（Lexer/Parser/Expression/CodeGen/Optimize/MatrixUtil 等约 2700 行）、
  11 个网络 payload、9 个命令参数类型、全部命令树、配置主体
- 平台层编写：入口类 ×2、Mixin ×5（按各自映射重写）、网络收发注册薄壳、目录注入

### 新增 common 抽象接口（每平台各一个实现）

| 接口 | 职责 | Fabric 实现 | NeoForge 实现 |
|---|---|---|---|
| `IPayloadSender` | 向世界内玩家发送 payload | `PlayerLookup.world` + `ServerPlayNetworking.send` | `PacketDistributor.sendToPlayers` |
| `IClientPayloadContext` | 客户端上下文（client/player） | `ClientPlayNetworking.Context` | `ClientPayloadContext` |
| `IParticleSpawner` | 生成粒子 | `CLIENT.particleEngine.createParticle` | 同（Mojang 名一致） |
| `IMessageSink` | 错误/消息上报 | `ClientMessageUtil`（聊天框） | 同 |
| `IPlatformPaths` | configDir / gameDir | `FabricLoader` | `FMLPaths` / `FabricLoader` 对应物 |

## 3. Yarn → Mojang 映射对照表（1.21.11，权威来源：本地 loom 映射缓存）

**类名**（intermediary → Mojang 可读名，来自 `mappings-base.tiny`）：

| Yarn 名（现状源码） | Mojang 官方名 | intermediary |
|---|---|---|
| `net.minecraft.client.particle.ParticleRenderer` | **`net.minecraft.client.particle.ParticleGroup`** | class_11938 |
| `net.minecraft.client.particle.ParticleManager` | **`net.minecraft.client.particle.ParticleEngine`** | class_702 |
| `net.minecraft.client.particle.Particle` | `Particle` | class_703 |
| `net.minecraft.core.particles.ParticleEffect` | `ParticleOptions` | — |
| `net.minecraft.util.Identifier` | `ResourceLocation` | — |
| `net.minecraft.text.Text` | `Component` | — |
| `net.minecraft.util.math.Vec3d` | `Vec3` | — |
| `net.minecraft.client.MinecraftClient` | `Minecraft` | — |
| `net.minecraft.core.particles.ParticleTypes$PACKET_CODEC` | `ParticleTypes.PACKET_CODEC` | — |
| `net.minecraft.network.packet.CustomPayload` | `CustomPacketPayload` | — |
| `net.minecraft.network.RegistryByteBuf` | `FriendlyByteBuf`/`RegistryFriendlyByteBuf`（按 1.21 版本确认） | — |

**方法/字段**（yarn → mojang，需从映射逐项提取，实施时用工具生成）：

| Yarn | Mojang |
|---|---|
| `ParticleManager.addParticle` | `ParticleEngine.createParticle` |
| `Particle.setMaxAge(int)` | `Particle.setLifetime(int)` |
| `Particle.setVelocityX/Y/Z` | `Particle.setXd/Yd/Zd` |
| `MinecraftClient.particleManager` 字段 | `Minecraft.particleEngine` 字段 |
| `ParticleRenderer.particles` 字段 | `ParticleGroup.<mojang 字段名>`（实施时查映射） |
| `ParticleRenderer.tickParticle` | `ParticleGroup.<mojang 方法名>`（实施时查映射） |
| `ParticleManagerAccessor.addTo` | `ParticleEngine.<mojang 方法名>`（实施时查映射） |

**重要推论**：main 分支（26.2，Mojang 映射）的 `ParticleManagerMixin` 目标为
`@Mixin(ParticleGroup.class)`——与本分支（1.21.11 Yarn）的 `@Mixin(ParticleRenderer.class)`
**是同一个 Mojang 类**（Yarn 曾将其改名 ParticleRenderer，26.2 的映射改回 ParticleGroup）。
因此 Mixin 逻辑跨版本稳定，只随映射换名。26.2 的 ParticleUtil 是 Mojang 命名的完整参考实现
（`CLIENT.particleEngine.createParticle`、`((IParticle)p).setRenderColor(...)`、`setLifetime` 等）。

**版本差异注意**（与 main 分支对比）：
- 26.2 中 `IParticle` 已合并颜色接口（`setRenderColor`），1.21.11 用 `setColor` + `setAlpha` 两步
- 1.21.0 / 1.21.1 **没有** ParticleRenderer/ParticleGroup 拆分（粒子 tick 还在 ParticleEngine 内），
  若支持这两个版本需要旧版 Mixin 集；`Permission.Level` 权限 API 也是 1.21.2+ 新增
- payload 体系（CustomPayload + PacketCodec）1.20.5+ 稳定，NeoForge 1.20.2+ 同构

## 4. Gradle 结构（loom 多模块）

```
settings.gradle      → pluginManagement(Fabric/NeoForge maven) + include common, fabric, neoforge
common/build.gradle  → fabric-loom；mappings = loom.officialMojangMappings()（Mojang 官方名）
                       minecraft + 平台无关依赖（ASM、guava、gson 随 loom 传递）
fabric/build.gradle  → fabric-loom；mappings = yarn 1.21.11+build.6
                       implementation(project(path: ':common', configuration: 'namedElements'))
                       fabric-loader / fabric-api / mixin 注解处理
neoforge/build.gradle→ fabric-loom 的 neoForge 支持；mappings = loom.officialMojangMappings()
                       neoForge "net.neoforged:neoforge:<mc>-<build>"（版本号实施时从 maven 解析）
                       直接依赖 common（同为 Mojang 映射，无需 remap）
```

- fabric 模块：loom 自动把 common 的 Mojang 名产物 remap 成 Yarn；Mixin 用 Yarn 名 + refmap
- neoforge 模块：Mixin 用 Mojang 名（与 common 一致）；neoforge.mods.toml 的 `[[mixins]]` 注册
- 产物名：`AnotherColorBlock-<mc>-<platform>.jar`（fabric / neoforge 各一个）

## 5. 实施步骤（1.21.11 分支）

1. Gradle 骨架：settings.gradle / 根 build.gradle / 三模块 build.gradle / gradle.properties 扩展
2. common 迁移：`git mv` 源码 → common/src/main/java，按第 3 节对照表把 MC 引用翻成 Mojang 名；
   新增 5 个抽象接口；`ParticleExConfig`/`ImageUtil`/`VideoUtil` 改为注入 Path
3. fabric 模块：入口类改为调用 common 注册器 + `FabricPlatform` 实现；5 个 Mixin 原样搬入；
   `fabric.mod.json` / `particleex.mixins.json` 移入
4. neoforge 模块：`@Mod` 入口 + `NeoForgePlatform` + 5 个 Mixin（Mojang 名）+ `neoforge.mods.toml`
5. 构建验证：`:fabric:build`（本机可达）；`:neoforge:build` 需 NeoForge maven 可达
   （2026-10-09 实测 maven.neoforged.net 超时，网络恢复后验证）
6. 版本矩阵：模板固化后按上游分支逐个复制适配（1.21 → 1.21.2 → ... → 26.2）

## 6. 验证清单

- [ ] `./gradlew :fabric:build` 通过（本机可行）
- [ ] `./gradlew :neoforge:build` 通过（网络恢复后）
- [ ] 产物 jar 内含 mixins.json 且 Mixin 目标类名与映射匹配
- [ ] 游戏内冒烟：`/particleex parameter` 爱心示例、图片粒子、分组命令
- [ ] 服务端指令 + 客户端渲染两端验证
