# 初始化顺序（Initialization Order）

> 2026-10-10 编写（对应 VibeCoding 检查点：启动顺序设计文档而非试错考古）。
> 以 26.2 分支为准；1.21/1.20 线除网络 API 差异（SimpleChannel vs CustomPacketPayload）外时序相同。

## 双端共通（平台入口，服务器+客户端都执行）

| # | 步骤 | Fabric | NeoForge |
|---|---|---|---|
| 1 | 配置加载 | `ParticleEx.onInitialize` → `ParticleExConfig.init()` | `NeoForgeEntry` 构造器 → `ParticleExConfig.init()` |
| 2 | 发送桥注入 | `Bridge.setSender(...)`（PlayerLookup + ServerPlayNetworking） | `Bridge.setSender(...)`（PacketDistributor） |
| 3 | payload 注册 | `PayloadTypeRegistry`（C2S+S2C 各 11） | `modBus` 监听 `RegisterPayloadHandlersEvent` → 11 个 `playToClient` |
| 4 | 命令注册 | `CommandRegistrationCallback.EVENT` → `ParticleExCommand.register` | `NeoForge.EVENT_BUS` 监听 `RegisterCommandsEvent` |
| 5 | 参数类型注册 | `ArgumentTypeRegistry` × 10 | （命令参数类型随分发器同步） |

**失败策略**：`ParticleExConfig.init()` 抛 IOException → `RuntimeException` 显式崩溃，不静默降级。

## 仅客户端（错误上报 + tick 驱动）

`ParticleExClient.onInitializeClient()`（fabric 的 `ClientModInitializer`）：

| # | 步骤 |
|---|---|
| 6 | `MessageBridge.setSink(new ClientMessageUtil())` —— 错误上报 sink 指向聊天框 |
| 7 | `ClientTickEvents.START_CLIENT_TICK` → `ParticleUtil.onStartClientTick()`（消费 `TICKSTARTTASKS` 队列） |
| 8 | `ClientTickEvents.END_CLIENT_TICK` → `ParticleUtil.onEndClientTick()`（消费 `TICKENDTASKS` 队列） |
| 9 | `ClientPlayNetworking.registerGlobalReceiver` × 11（S2C 收包 → `ClientNetworkHandler`） |

**关键约束（发布级 bug 修复点）**：
- 服务器端**不得**触碰 `net.minecraft.client.*` —— `MessageBridge.setSink` 仅在第 6 步（客户端入口）注册。
  服务器端保持 `MessageBridge` 默认 sink（`printStackTrace` 到服务端日志）。
- neoforge 的 11 个 S2C handler 统一 `ctx.enqueueWork(() -> ...)` 切回主线程（网络线程要求）。

## 运行时链路

```
命令（/particleex ...）
  → ParticleExCommand（common）
  → Bridge.sendToPlayers(level, payload)          [发送桥，第 2 步注入]
  → 平台 sender：
      fabric:   PlayerLookup.level(world) → ServerPlayNetworking.send(player, payload)
      neoforge: PacketDistributor.sendToPlayersInDimension(world, payload)
  → 客户端 ClientNetworkHandler 收包 → 渲染 / 调度 tick 任务
  → ParticleUtil tick 队列在 START/END tick 消费（第 7/8 步驱动）

异常路径：
  → MessageBridge.report(t) → sink：
      客户端 = ClientMessageUtil（聊天框红色报错）
      服务端 = 默认 printStackTrace（服务端日志）
```

## 平台桥默认值（未注入时的兜底）

| 桥 | 默认 | 注入点 |
|---|---|---|
| `Bridge.sender` | no-op（命令层可独立编译） | 第 2 步（双端） |
| `MessageBridge.sink` | `printStackTrace` | 第 6 步（仅客户端） |

## 相关

- 网络 payload 清单：`common/.../network/payload/`（11 个类）
- 命令树权威源对照：`docs/testing/manual-checklist.md`
