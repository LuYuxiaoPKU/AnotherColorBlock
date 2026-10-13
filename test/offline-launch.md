# 离线直启游戏（绕过 HMCL GUI）经验

> 2026-10-13 实测（26.1/26.1.1 NeoForge 实例）。目的：手动拉起游戏做实机测试，适用于 HMCL 启动器 GUI 不可用 / 需要加载本地替换修复（如 jij 缓存置换）的实例。

## 为什么需要

- **HMCL `--launch` 参数在 dev 版本不可用**：实测 `hmcl-dev-3.6.11.273.exe --launch ...` 启动的是**第二个 HMCL GUI 进程**，不是游戏（java 进程是 `java -jar hmcl-dev-*.exe`，无游戏 JVM）。
- 26.1/26.1.1 实例需要先替换 FML jij 缓存里的 mixinextras（0.5.3→0.5.5）才能启动，用 HMCL GUI 每次校验可能回写；离线 bat 直启完全绕开启动器逻辑。

## 原理

HMCL 生成（或 HMCL 日志里打印）的 `launch.bat` 包含**完整 java 命令行**，是"离线化"的最佳模板：

```
java <JVM 参数> -javaagent:<authlib-injector> -Dauthlibinjector.* <classpath 参数> net.neoforged.fml.startup.Client --gameDir ... --accessToken <access token> --clientId ${clientid} --xuid ${auth_xuid} ...
```

离线化 = 删掉认证相关参数 + 令牌占位。**注意：authlib-injector 是 LittleSkin 本地的合法 javaagent**（不是残留病毒），classpath 里的 `authlib-7.0.63.jar` 也是合法依赖，不要误删。

## 转换步骤（launch.bat → offline bat）

1. 拿到模板：HMCL 实例目录下生成的 launch.bat，或 HMCL 日志中打印的启动命令行。
2. **删除** `-javaagent:...authlib-injector...` 与全部 `-Dauthlibinjector.*` 参数。
3. `--accessToken <access token>` → `--accessToken 0`。
4. `--clientId ${clientid}` → `--clientId HMCL-Offline`。
5. `--xuid ${auth_xuid}` → `--xuid 0`。
6. main class 保持 `net.neoforged.fml.startup.Client`（NeoForge 26.x）。
7. `--gameDir` 指向实例目录（如 `versions/26.1-NeoForge`），不要指向 HMCL 根。
8. 保留原有 JVM 参数（Xmx、**`--add-opens` 等 neoforge 必需项不要动**）；如需验证组件版本可追加输出（如 `MixinExtrasServiceImpl(version=0.5.5)` 会在启动日志打印）。

生成件：`test/launch-26.1-offline.bat`（26.1 实例），可复制改名换 gameDir 复用于其他实例。

## 运行与日志判定

```bash
cd <实例目录> && cmd //c "C:\00_Data\RGM\AnotherColorBlock-CC\test\launch-26.1-offline.bat" > launch261.log 2>&1
```

- **stdout 是全量日志**：离线直启不写 `latest.log`（实例 logs/latest.log 仍是上一次崩溃的内容，判定时不要读它），游戏所有输出走 stdout，重定向到文件后即可 grep。
- git-bash 里 `cmd //c` 必须以**绝对路径**传 bat（cwd 会重置，相对路径找不到）。
- 判定内容：`MixinExtrasServiceImpl(version=0.5.5)`（jij 替换生效）、进档（entity id）、`Failed to load function`（预期 errprobe 失败直出）、命令执行零 Failed。
- 玩家进世界后客户端日志在 stdout 里；离线令牌会产生 401/SignedJWT 噪音，属豁免。
- 关服：等玩家退出后窗口/进程自然退出即可（也可 Ctrl-C 或杀进程），退出前先确认测试已收尾。

## 相关链接

- jij 缓存替换法（mixinextras 0.5.3→0.5.5）：见 test-progress.md 26.1.1 / 26.1 行。
- 实例目录是 HMCL 影子实例结构，`versions/<instance>` 可独立操作。
