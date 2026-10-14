# neoforge 参数类型注册崩溃调研：为什么 1.21.4 炸、1.21.5+ 不炸

> 2026-10-14，1.21.4-NeoForge 进档崩溃（`commands` 包 `VarInt too big`）七轮探针 + 字节码对账收口。
> 结论适用于全部带 neoforge 模块的分支的 `registerArgumentTypes` 实现。

## 一、崩溃因果链（1.21.4 实证）

1. mod 构造期用 `MappedRegistryAccessor` **字段级直塞** `COMMAND_ARGUMENT_TYPE` 的 `byId`/`toId`（9 个自定义 ArgumentTypeInfo）。
2. **直塞不完整**：vanilla `MappedRegistry` 内部有 5 套结构——`byId`（id→Holder）、`toId`（value→id）、`byValue`、`byKey`、`byLocation`。直塞只动了前两个。
3. NeoForge 对 `COMMAND_ARGUMENT_TYPE`（在 `VANILLA_SYNC_REGISTRIES` 集合中，`NeoForgeRegistriesSetup.modifyRegistries` 对其 `setSync(true)`）在注册收尾 `freezeData` 时拍 **`RegistrySnapshot`**。
4. **Snapshot 的 ids 映射从 `Registry.keySet()` 派生**（字节码实证：`RegistrySnapshot.<init>` 遍历 `keySet()`）——`keySet()` 走 `byKey`/`byLocation`，**不含直塞条目** → snapshot 丢掉 9 个自定义类型。
5. 客户端进档时 NeoForge 走 registry sync：`ClientPayloadHandler.handle(FrozenRegistrySyncCompletedPayload)` → `RegistryManager.applySnapshot(map,...)` → **私有 `applySnapshot(MappedRegistry, RegistrySnapshot, ...)` 对 registry `unfreeze → clear → registerIdMapping` 全量重建**（字节码实证）——**服务端写包时探针看到的"健康状态"（roundtrip=true、holeScan=0）是直塞后的临时态；客户端读包时的 registry 是被 snapshot 重建后的态——直塞条目已丢失**，`byId(62)` 返回 null。
6. 读侧 `read(buf, flags)`：`byId(id)` null → `aconst_null; areturn`（stub=NULL，探针实证）→ 该节点的 serializer 数据没被消费 → **字节流从此错位** → 后续 `readVarIntArray` 读到垃圾 → `VarInt too big` 进档即断。

## 二、为什么 1.21.5+ 不炸（同代码）

| 因素 | 1.21.4 (NeoForge 21.4.158 / FML 6.0.18) | 1.21.5 (NeoForge 21.5.98 / FML 9.0.x) |
|---|---|---|
| `VANILLA_SYNC_REGISTRIES` 含 COMMAND_ARGUMENT_TYPE | ✓ | ✓ |
| `setSync(true)` 逻辑 | 相同 | 相同 |
| `RegistrySnapshot` 从 keySet() 派生 | ✓（丢直塞条目） | ✓（丢直塞条目） |
| **客户端 sync 是否 applySnapshot 重建** | **✓（FrozenRegistrySync 三段协议，重建 registry）** | **机制存疑——同代 FML 9.0 系列下 1.21.5~1.21.8 均实测通过** |

**1.21.5~1.21.8 实测通过的最可能解释**（按可能性排序）：

1. **1.21.5+ 起这些版本的 NeoForge 的 registry sync 时机/协议变化**：1.21.5 的 `client/registries/ClientRegistryManager` 从 `registries/` 挪到 `client/registries/`（jar 清单 diff 实证），sync 流程重构——**integrated server（单机）路径可能不再走 FrozenRegistrySnapshot 重建**（或重建发生在命令树包**之前**且重建源包含直塞条目）。
2. **1.21.5+ 的 ClientboundCommandsPacket 读侧对 null info 的容错变化**：未逐一反编译，但 1.21.5 实测同样自定义参数类型的命令树能同步成功，说明读侧 registry 认得这 9 个 id——**读侧 registry 状态没有像 1.21.4 那样被重建清掉直塞**。
3. `IdMappingEvent` 机制在 1.21.5 被删除（jar diff 实证 1.21.4 有 / 1.21.5 无）——1.21.4 的 `fireRemapEvent` 与旧式 id 管理耦合更深，重建路径更激进。

**结论：1.21.5+ 是"机制上仍不完整、但该版 NeoForge 未触发致命重建"的侥幸正确；26.x 是协议上直塞足够（读侧只查 BY_CLASS 不查注册表 id）的正确。**

## 三、最终修法（已在 1.21.4 实测闭环）

`RegisterEvent`（mod bus）+ `Registry.register`——NeoForge 官方注册窗口：
- 在 `BuiltInRegistries.COMMAND_ARGUMENT_TYPE` 的 RegisterEvent 里逐个 `Registry.register(registry, rl("color4"), info)`。
- 同步维护全部 5 套内部结构 + 进 NeoForge 快照/sync 管理体系 → 客户端重建后 id 依然正确。
- mod 构造期直接 `Registry.register` 不行（`Registry is already frozen` 实证）；字段直塞更不行（本文根因）。

## 四、行动项

| 分支 | 现状 | 处理 |
|---|---|---|
| 1.21.4 | ✅ RegisterEvent 修复，实测闭环 | 基准 |
| **1.21.1/1.21.2/1.21.3** | 直塞（高危，与 1.21.4 同 FML 6.x 机制，大概率同炸） | **必须 backport RegisterEvent 修法** |
| 1.21.5~1.21.8 | 直塞（侥幸正确） | 建议 backport 统一（防上游 patch 变化） |
| 1.21.9~1.21.11 | 直塞（实测通过；1.21.11 另有注册移植 8af00e2） | 建议 backport 统一 |
| 26.1/26.2/26.3 | 直塞（协议上正确：读侧只查 BY_CLASS） | 可不动；若统一也无害 |

## 五、探针残留清理

1.21.4 分支 `c1e0f7f` 已移除全部临时探针（SERIALIZE 日志 + readNode mixin）。
