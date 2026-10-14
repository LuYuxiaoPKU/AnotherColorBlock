# 版本分支操作手册

## 分支模型

每个 MC 版本一个分支（`1.21`、`1.21.2`、`1.21.4`…`26.1`…），分支内是 common/fabric/neoforge 三模块布局。
版本差异全部集中在 `gradle.properties`（home 分支为 `1.21.11`，作为新版本的复制模板）。

## 版本常量获取（权威渠道）

| 常量 | 来源 | 说明 |
|---|---|---|
| `minecraft_version` | 版本目标本身 | 如 `1.21.11`、`26.1.2` |
| `loader_version` | maven.fabricmc.net | fabric-loader 最新稳定 |
| `fabric_version` | maven.fabricmc.net | fabric-api，形如 `0.141.6+1.21.11` |
| `neo_form_version` | **格式 `<MC>-<时间戳>`**，如 `1.21.11-20251209.172050` | 用 GitHub 代码搜索别家已落地项目取值最可靠：`gh search code "neoform_version=1.21.11"`；上游 neoforged/NeoForge 主仓库 `gradle.properties` 的 `neoform_version` 值**缺前缀**（快照用），发布版必须补 `<MC>-` 前缀 |
| `neoforge_version` | projects.neoforged.net / 上游分支 | 如 `21.11.0` |

## 已知网络限制（本机）

- `maven.neoforged.net`、`projects.neoforged.net`、`docs.neoforged.net` 本机不可达（CI 可达）
- 本机可取：GitHub（raw/gh api）、maven.fabricmc.net 不可达（curl 测试 2026-10-09）
- 验证版本存在性的轻量途径：GitHub 代码搜索（`gh search code` / `gh api search/code`），找同版本落地项目抄版本号

## 版本验证流程（复制新分支后）

1. 核对 `gradle.properties` 全部常量
2. 用本机 loom 缓存（若已有该版本）或待 CI 解析，按 [api-verification.md](api-verification.md) 流程核验所有 mixin 注入点与 @Accessor/@Invoker/@Shadow 目标
3. push 后跑 GitHub Actions（matrix fabric/neoforge，全绿即模板可用）
4. 有差异的 API 按 api-verification.md 记录

## 跨版本已知差异（模板分支施工前必查项）

| 项 | 1.21.11 | 可能差异的版本 |
|---|---|---|
| Particle 生命周期方法 | `remove()` | 1.20.x 为 `markDead()` |
| ParticleGroup 队列初始化 | `EvictingQueue.create(16384)`（Guava） | 更早版本为 `ArrayDeque` |
| constant 参数注册 | `SingletonArgumentInfo.contextFree` | 1.21 前为 `ConstantArgumentTypes` |
| 权限 API | `PermissionLevel`（枚举） | 1.21.6 前为 `requires(2)` 整数等级 |
| Minecraft 世界字段 | `level` | yarn 名 `world`（映射转换时） |
## 分支简并评估（2026-10-15，决策：不简并）

**背景**：fabric 线 30 站实机全绿后，逐级 diff 评估了 23 分支的简并可能性。

**fabric 侧实测覆盖矩阵（已证）**：1.17.1→(1.17,1.17.1)、1.18.2→(1.18,1.18.1,1.18.2)、1.19.2→(1.19,1.19.1,1.19.2)、1.19.4→(1.19.3,1.19.4)、1.21.1→(1.21,1.21.1)、26.1→(26.1,26.1.1,26.1.2)——字节码同 jar 系 + range/patched 实证。

**fabric 侧相邻断代点**（1.18.2→1.19.2：ArgumentTypeRegistry 重构+CommandBuildContext；1.19.2→1.19.4：ParticleArgument/BuiltInRegistries；1.20.1→1.20.2：CustomPacketPayload.id()；1.20.4→1.20.5：StreamCodec；1.21.8→1.21.9：mixin 字段搬家 62xxx；1.21.10→1.21.11：Identifier 换名）。

**决策：不简并（维持 23 分支）**。核心理由是 neoforge 侧无法同步简并：
1. 1.19.4 及以下分支无 neoforge 模块（1.20.1 初建），1.19.x fabric 简并后 neoforge 缺位
2. 1.21.1~1.21.3 neoforge 参数注册直塞 bug（FML FrozenRegistrySync 快照重建丢条目→进档崩），1.21.5+ 才侥幸——neoforge 产物跨这段必踩
3. 26.1 的 mixinextras 0.5.3 jij 崩溃等 loader 层断代仅 neoforge 侧存在
4. fabric 侧同 jar 系不代表 neoforge 同系（neo_form/neoforge_version 独立断代）

已实测的跨版本兼容作为事实保留（用户拿 1.18.2 fabric jar 装 1.18 可跑），但分支粒度维持每断代点一分支，neoforge 差异风险不为省分支数买单。若未来 neoforge 模块全版本铺开且逐版实测，可重开此评估。

## 分支 fabric 产物支持版本 × Java 要求（2026-10-15，minecraft.wiki 核对）

**Java 分界（wiki 各版本页 Minimum Java version 原文）**：1.17 首个要求 Java 16；1.18 首个 Java 17；1.20.5 首个 Java 21（同步首个强制 64 位，与 StreamCodec 网络重构同版本）；26.1 首个 Java 25（Microsoft OpenJDK 25）。fabric.mod.json 不显式声明 java depends——Java 要求由 MC 版本 json 强制，mod 无需重复。

| 分支 | fabric 产物支持版本 | Java |
|---|---|---|
| 1.17.1 | 1.17、1.17.1 | 16 |
| 1.18.2 | 1.18、1.18.1、1.18.2 | 17 |
| 1.19.2 | 1.19、1.19.1、1.19.2 | 17 |
| 1.19.4 | 1.19.3、1.19.4 | 17 |
| 1.20.1 | 1.20、1.20.1 | 17 |
| 1.20.2 | 1.20.2 | 17 |
| 1.20.4 | 1.20.4 | 17 |
| 1.20.5 | 1.20.5 | 21 |
| 1.20.6 | 1.20.6 | 21 |
| 1.21.1 | 1.21、1.21.1 | 21 |
| 1.21.2~1.21.8 | 各自版本 | 21 |
| 1.21.9 / 1.21.10 | 各自版本 | 21 |
| 1.21.11 | 1.21.11 | 21 |
| 26.1 / 26.2 / 26.3 | 各自版本 | **25** |

注：支持版本为 fabric 线 30 站实测覆盖矩阵（含裸 1.17/1.18/1.19/1.20/1.21 patched/正式分支实测），neoforge 侧各分支仅支持自身版本（未做跨版本实测）。
