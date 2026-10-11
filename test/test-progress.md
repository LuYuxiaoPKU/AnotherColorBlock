# 实机统一测试进度（双 Agent 协同共享文档）

> 本文件是「Fabric mixin 半 remap 修复」与「参数注册外推」两会话的**唯一测试进度同步源**。
> 任何一方更新后，在会话内口头同步一句"test-progress 已更新"即可。
> 统计口径：**分支 / jar sha / 平台 / 进档 / 命令 / 网络 payload / 备注**。

## 当前焦点

- **命令不可用问题排查中**（26.x neoforge）：现象「未知或不完整的命令。错误见下」+ 行尾 `<--[此处]`。
- 怀疑点（bisect 中）：26.x 权限 API 重构（`Commands.LEVEL_GAMEMASTERS` 从 int 变 `PermissionCheck` 接口、requires 变 `PermissionProviderCheck`）→ requires=false 节点在 ClientboundCommandsPacket 剪枝 → 客户端树无命令。1.21.6 前后曾有 requires(2)→PermissionLevel 枚举的已知差异。
- 26.3 命令 dump 日志版（329ab481）已就绪待部署，可一锤定音（注册后根命令树直接打印）。

## 测试记录

| 日期 | 分支 | jar sha | 平台 | 进档 | 命令 | 网络 payload | 备注 |
|---|---|---|---|---|---|---|---|
| 10-11 09:44 | 26.3 | **旧构建 01:21** | NeoForge | ❌ 版本范围拒绝 | - | - | mods 目录 jar 倒置（26.3 jar 被 .keep 禁用、26.2 jar 启用）；26.2 分支范围 [26.2,26.3) 不含 26.3 |
| 10-11 09:44 | 26.3 | 6770056d（首测 CI 产物） | NeoForge | ❌ Unrecognized arg type | - | - | jar 实际为参数注册前旧代码（无 registerArgumentTypes 字节码） |
| 10-11 09:49 | 26.3 | 6770056d（CI 产物） | NeoForge | ✅ entity id 1 | ❌ 未知或不完整的命令 | 未测 | 参数注册生效；命令问题排查中（权限 API 重构嫌疑） |
| 10-11 09:47 | 26.3 | 329ab481（debug dump 版） | NeoForge | 待测 | 待测 | 待测 | 已下载到 /tmp/acb-263dbg，26.2 测完部署 |
| 10-11 09:55 | 26.2 | 3876aba（最新 CI 产物） | NeoForge | **测进行中** | 待测 | 待测 | 已部署启用；重点看命令是否同样不可用 |

## 部署/环境速查

- 实例目录：`C:/00_Data/RGM/AnotherColorBlock-CC/test/.minecraft`
- 版本实例：versions/ 下 26.1/26.2/26.3 / 1.21.x / 1.20.x 均有 Fabric+NeoForge 双平台
- mods 目录：当前启用 `particleex-neoforge-26.2-1.0.0.jar`（其余 .disabled/.old 备份）
- CI 产物下载：`gh run download <runId> --repo LuYuxiaoPKU/AnotherColorBlock --name particleex-neoforge-<sha> --dir <dir>`
- 日志：`test/.minecraft/logs/latest.log`（GBK 编码，读时用 `decode('gbk')`）