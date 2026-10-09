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
| 运行时 JDK | Java 21 | **26.x 起要求 Java 25**（Gradle ≥ 9.x；CI 动态 JDK 已就绪） |
| loom 版本 | 1.13.6 | **26.x 用 1.18-SNAPSHOT**（1.17 线不支持 26.x 映射获取） |
| 官方映射 | `loom.officialMojangMappings()` 可用 | **26.x 下需 loom 1.18 线**才能获取（否则报 Failed to find official mojang mappings） |

## 26.x 特有（2026-10-10 实证）

- 26.3：fabric loader 0.19.5、fabric-api 0.162.0+26.3、neoform 26.3-1、neoforge 26.3.0.43-beta（beta 期，后续可能升）
- 26.3 的 NeoForge 仍 beta：`neoforge_version=26.3.0.43-beta`（AE2 引用）