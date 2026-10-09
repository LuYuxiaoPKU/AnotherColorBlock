# AnotherColorBlock CI 自动化冒烟设计(GitHub Actions 参考)

> 本文是**设计文档**,不包含实际落地的工作流文件。
> 目标:每次推送/PR 自动验证两类可无头化的问题——
> ① 服务端能否干净启动(命令树注册、模组加载、无异常);② 服务端执行路径
> (命令解析 → payload 编码 → 发送)不炸。
> 明确边界:**客户端渲染(粒子可见性、颜色/寿命/速度表达式效果、Mixin 注入、
> 图片/视频显示)无法在 CI 验证,一律划归人工**——见第 4 节清单。

配套:`docs/testing/smoke-test.md`(人工冒烟规程)、`docs/testing/manual-checklist.md`。

---

## 1. 通用设计原则

1. **日志驱动**:一切断言基于服务端 `logs/latest.log` 与服务进程 stdout,
   不引入模组内测试代码(当前阶段不改源码)。
2. **启动就绪信号**:轮询日志直到出现
   `Done (…)s! For help, type "help"`(MC 1.21 系 DedicatedServer 启动完成行),
   超时(建议 300 s)即失败。
3. **无异常断言**:对日志做关键字扫描(区分大小写):
   `ERROR`、`Exception`、`Caused by`、`FAILED`、`mixin apply failed`、
   `Packet type already registered`、`ClassCastException`。
   > 注意:`WARN` 不判失败(Mod 加载常见无关警告),但 `ClassCastException`
   > 与 `Packet type already registered` 是本模组 b904f4c 回归红线,命中即失败。
4. **命令执行验证**:优先 **RCON**(输出可回读、断言稳定);
   备选 **stdin 管道**(`echo "command" | ...`,仅 nogui 模式可用,输出为日志行)。
   - RCON 配置:`server.properties` 设
     `enable-rcon=true`、`rcon.port=25575`、`rcon.password=<随机串>`(CI 中经 Secret/Env 注入)。
   - 本模组命令大多**无反馈输出**(见 smoke-test.md 0.3),故断言"命令执行成功"
     用两条:① 退出码/无异常;② 有反馈的命令断言内容
     (`functionlist` 反馈含 `sin(a),`)。
5. **退出**:执行 `stop` 并等待进程自然退出,断言退出码 0;超时则 kill 判失败。
6. **环境**:JDK 21(项目 toolchain);本项目 `org.gradle.daemon=false`,
   每次 CI 是新 JVM,构建耗时需容忍;产物经由 `./gradlew :<module>:build` 产出后,
   测试直接复用 `build/libs/` 的 jar。
7. **版本矩阵**:多平台改造固化到各版本分支后,workflow 以 matrix 展开
   (`mc: [1.21.11, ...]` × `platform: [fabric, neoforge]`);
   当前阶段只对 1.21.11 分支生效。

---

## 2. Fabric 冒烟 CI

### 2.1 启动方案:`runServer`

- fabric 模块已配置 loom `runs.server`(`runDir('runs/server')`),对应 Gradle
  任务 `:fabric:runServer`,启动的是带本模组 + Fabric API 的
  **DedicatedServer**(服务端进程,天然不含客户端渲染)。
- 首次启动前预写运行目录(CI 每次清空重建,保证幂等):
  - `runs/server/eula.txt` → `eula=true`;
  - `runs/server/server.properties` → 开 RCON 的 3 项(见 1.4);
  - 若 RCON 不可用,给 `server` run 加 `--nogui` 以启用 stdin
    (设计建议:在 fabric 模块 `loom.runs.server` 增加
    `programArgument '--nogui'`,不影响 IDE 使用)。

### 2.2 步骤序列(参考)

```text
1) ./gradlew :fabric:build            # 产出 particleex-fabric-1.21.11-<v>.jar
2) 预写 runs/server/eula.txt、server.properties
3) ./gradlew :fabric:runServer > server.out 2>&1 &    # 后台
4) 轮询 runs/server/logs/latest.log 出现 "Done ("     # 超时 300s → fail
5) 扫描日志:无 ERROR/Exception/ClassCastException/Packet type already registered
6) rcon-cli(如 mcrcon)执行(命令不带前导 /):
   particleex functionlist           # 断言输出含 "sin(a),"  → 命令树+权限+反馈通道 OK
   particleex normal minecraft:heart 0 8 0 1 0 1 1 0 0 0 0 5 200   # 断言无异常(服务端执行路径)
   particleex group remove nonexist  # 断言无异常(组命令缺组不崩)
   particleex clearparticle          # 断言无异常
7) 扫描日志(第二次):无新增异常
8) 执行 stop,等待退出,断言 exit code 0
```

### 2.3 覆盖能力与盲区

- **能自动验证**:模组加载、`/particleex` 命令树注册(9 个自定义参数类型经
  `ArgumentTypeRegistry` 注册,加载失败会直接启动报错)、命令解析与参数校验
  (非法表达式字符串在服务端仅作字符串透传,**不会**在服务端解析——
  表达式引擎在客户端,见下)、payload 构造与 `ServerPlayNetworking.send`
  编码路径(**不含**真正网络往返,单进程内无玩家)。
- **不能自动验证(必须人工)**:所有客户端渲染;表达式引擎
  (Lexer/Parser/CodeGen)实际求值;Mixin 注入;payload 客户端解码;
  多玩家广播行为。均在 smoke-test.md 第 2/4 节与 manual-checklist.md。
- **推荐(未来扩展,不改源码前提下不做)**:在 common 为纯计算部分
  (表达式引擎)引入 JUnit 单测即可在 CI 覆盖引擎语义——比启动服务器
  更便宜、断言更细;服务端启动冒烟退化为"加载 + 命令注册"两件事。

### 2.4 参考 workflow 骨架(仅示意,不落地)

```yaml
# .github/workflows/fabric-smoke.yml(设计示意,勿直接提交)
name: fabric-smoke
on: [push, pull_request]
jobs:
  smoke:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '21' }
      - run: ./gradlew :fabric:build
      - name: prepare server dirs
        run: |
          mkdir -p runs/server
          printf 'eula=true\n' > runs/server/eula.txt
          printf 'enable-rcon=true\nrcon.port=25575\nrcon.password=%s\n' \
            "${{ secrets.RCON_PASSWORD }}" > runs/server/server.properties
      - name: boot server
        run: ./gradlew :fabric:runServer > server.out 2>&1 &
      - name: wait for Done
        run: timeout 300 bash -c 'until grep -q "Done (" runs/server/logs/latest.log; do sleep 2; done'
      - name: assert clean boot
        run: ! grep -Ei 'Exception|Caused by|ClassCastException|Packet type already registered|mixin apply failed' runs/server/logs/latest.log
      - name: rcon checks
        run: |
          mcrcon -H 127.0.0.1 -P 25575 -p "${{ secrets.RCON_PASSWORD }}" \
            -c 1 'particleex functionlist' | grep 'sin(a),'
          mcrcon -H 127.0.0.1 -P 25575 -p "${{ secrets.RCON_PASSWORD }}" -c 1 'particleex clearparticle'
      - name: shutdown
        run: |
          mcrcon -H 127.0.0.1 -P 25575 -p "${{ secrets.RCON_PASSWORD }}" -c 1 'stop' || true
          wait; test $? -eq 0
```

---

## 3. NeoForge 冒烟 CI

### 3.1 启动方案:`gameTestServer` + `neoforge.enabledGameTestNamespaces`

- neoforge 模块 `build.gradle` **已配置**:
  - `runs.gameTestServer`(`type = 'gameTestServer'`,目录 `runs/gametest`);
  - `configureEach` 中已设
    `systemProperty('neoforge.enabledGameTestNamespaces', project.mod_id)`。
  对应 Gradle 任务 `:neoforge:runGameTestServer`。
- **`neoforge.enabledGameTestNamespaces` 说明**:NeoForge GameTest 框架按
  命名空间(mod id)筛选要执行的 `@GameTest` 方法;值为 `particleex` 即
  "只运行本模组注册的 GameTest"。当前模组**未编写任何 GameTest 方法**,
  因此 gameTestServer 行为等价于"干净的服务端启动 + 命令树注册检查"——
  与 Fabric 冒烟的目的相同;区别是 gameTestServer 具备**执行 @GameTest 的能力**,
  为将来把可服务端化的验证(表达式引擎纯计算、payload 编解码对称性)写成
  GameTest 预留了通道。
- 扩展建议(未来):在 common/neoforge 添加少量 `@GameTest`(例如:
  构造 `NormalPayload` → 编解码往返 → 字段一致;表达式引擎计算
  `sin(PI/2)==1`),即可让 `gameTestServer` 把这些断言纳入 CI。
  **注意**:服务端 GameTest **无法**验证客户端渲染,此类测试只覆盖
  服务端可执行逻辑。

### 3.2 步骤序列(参考)

与 2.2 相同骨架,差异:

```text
1) ./gradlew :neoforge:build            # 需 maven.neoforged.net 可达
   # 当前 settings.gradle 中 include 'neoforge' 被注释,网络恢复后启用
2) 预写 runs/gametest/eula.txt、server.properties(开 RCON)
3) ./gradlew :neoforge:runGameTestServer > server.out 2>&1 &
4) 轮询日志 "Done ("(NeoForge 服务端日志格式与 Vanilla 一致)超时 300s
5) 日志断言(同 2.2 第 5 步,另加 'neoforge.mods.toml' 加载失败关键字)
6) RCON:particleex functionlist → 断言含 "sin(a),";clearparticle → 无异常
7) stop 退出,断言退出码 0
```

- 注意:gameTestServer 在**无 GameTest 方法**时仍正常启动服务端、正常执行
  命令(本项目现状);若将来启用 GameTest 方法,启动后会先执行测试再进入
  idle,日志断言与 RCON 时序需相应调整(先等 "Tests finished" 类日志)。
- NeoForge 侧无玩家环境,RCON 是唯一可靠的命令结果回读通道。

### 3.3 双平台冒烟对照

| 环节 | Fabric | NeoForge |
|---|---|---|
| 启动任务 | `:fabric:runServer` | `:neoforge:runGameTestServer` |
| 就绪信号 | `Done (` 日志 | 同 |
| GameTest 能力 | 无(可用 loom 自带 test 需额外配置) | 有(enabledGameTestNamespaces 已配) |
| 命令回读 | RCON / stdin | RCON |
| 产物 | `particleex-fabric-*.jar` | `particleex-neoforge-*.jar` |
| 额外前置 | 无 | maven.neoforged.net 可达、settings.gradle 启用 neoforge |

---

## 4. 必须人工验证(CI 不可覆盖)清单

以下项目在**任何自动化方案下都不可验证**,发布前必须按
`docs/testing/smoke-test.md` 与 `manual-checklist.md` 人工执行:

1. 粒子**渲染可见性**与图案形状(爱心轮廓、球壳空心、甜甜圈环面);
2. 颜色/透明度、寿命(age/-1 永久)、`destroy` 销毁的实际观感;
3. 速度表达式(轨道旋转、渐变)的逐 tick 表现;
4. 5 个客户端 Mixin 的注入生效(表现为 1~3 全部异常,见 smoke-test.md 5.1);
5. 图片/视频粒子的像素级显示、旋转/翻转/矩阵变换、clearcache 重载;
6. 分组 `group remove/change` 的可见增删改效果;
7. **双端联机全流程**(smoke-test.md 第 4 节,b904f4c 回归的
   "独立服务端发包 → 客户端不断线不崩溃"场景必须有真实网络往返);
8. 性能:大批量粒子帧率、`maxParticleCount` 上限、长时间挂机内存。

> CI 能给的承诺:每次提交都保证"服务端能启动、命令树能注册、服务端执行路径
> 无异常";剩下的视觉承诺由人工回归清单守住。

---

## 5. 风险与注意事项

1. **网络依赖**:NeoForge 构建依赖 `maven.neoforged.net`(2026-10-09 实测超时),
   CI 中该 job 失败不一定是代码问题;建议 `neoforge` job 设
   `continue-on-error` 或单独 schedule,并在描述中标注。
2. **Gradle 冷启动**:`org.gradle.daemon=false`,每次约 1~3 分钟构建;
   loom 下载 minecraft/yarn 缓存建议 CI cache
   (`~/.gradle/caches` + `~/.gradle/loom-cache`)。
3. **日志乱码/编码**:Windows runner 需 `JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8`,
   否则中文/特殊字符日志断言易误判。
4. **RCON 密码**:必须走 Secret,禁止明文进仓库(本仓库无密钥管理时,
   可用 CI 临时随机串 + 输出捕获)。
5. **超时与僵尸进程**:任何步骤超时都要 kill 整个 gradle 进程树
   (`pkill -P` 或 actions 的 `timeout-minutes`),否则 runner 挂死。
6. **版本矩阵膨胀**:每版本分支一套 workflow 会显著增加 CI 时间;
   建议"最新版全量冒烟 + 其余版本仅启动检查"的分级策略。
7. **诚实标注**:本模组"服务端指令 + 客户端渲染"架构决定了 CI 覆盖率上限
   是服务端半边;文档与 PR 描述中应如实说明,避免把 CI 绿当成全部测试通过。