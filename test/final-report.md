# AnotherColorBlock 全量实机测试总报告

> 收官日期：2026-10-16 ｜ 仓库：YuEr159/AnotherColorBlock ｜ 权威明细源：[test-progress.md](test-progress.md)（本目录，git 忽略、需 `git add -f`）
>
> 本报告是双会话协同（Fabric 线 + NeoForge 线）逐版本实机回归的**汇总层文档**：只收录结论与跨版本方法论，逐站修复细节见进度表登记行。

---

## 一、总覆盖矩阵（60 站全绿）

五项判定（进档 entity id / 命令函数执行 / errprobe 预期失败 / 粒子目视 / 日志零异常）全部达标。

**Fabric 线 36 版本（37 站，含 1.19.2 depends 修复回测）**：
`26.3 → 26.2 → 26.1.2 → 26.1.1 → 26.1 → 1.21.11 → 1.21.10 → 1.21.9 → 1.21.8 → 1.21.7 → 1.21.6 → 1.21.5 → 1.21.4 → 1.21.3 → 1.21.2 → 1.21.1 → 1.21 → 1.20.6 → 1.20.5 → 1.20.4 → 1.20.2 → 1.20.1 → 1.20 → 1.19.4 → 1.19.3 → 1.19.2 → 1.19.1 → 1.19 → 1.18.2 → 1.18.1 → 1.18 → 1.17.1 → 1.17 → 1.16.5 → 1.15.2 → 1.14.4`

**NeoForge 线 23 版本（23 站）**：
`26.3 → 26.2 → 26.1.2 → 26.1.1 → 26.1 → 1.21.11 → 1.21.10 → 1.21.9 → 1.21.8 → 1.21.7 → 1.21.6 → 1.21.5 → 1.21.4 → 1.21.3 → 1.21.2 → 1.21.1 → 1.21 → 1.20.6 → 1.20.5 → 1.20.4 → 1.20.3 → 1.20.2 → 1.20.1`

**下边界定案**：1.13.2 / 1.12.2 放弃（用户拍板，生态真空实证）——官方 Fabric 工具链无 1.13.2（intermediary/yarn/loader-meta/fabric-api 全线缺失）；legacy-fabric-api 无 1.13.x 发行；4 个依赖能力（CommandRegistrationCallback 等）需裸 mixin 重写平台层；1.12.2 连 Brigadier 与数据包 function 都不存在。**fabric 线全历史覆盖定格 37 站。**

**边缘版本覆盖方式**：裸版本 1.21/1.20/1.19/1.18/1.17 与 1.19.3、1.19.1、1.18.1 由相邻分支产物（depends range 或本地 patched depends）直接覆盖，实机验证同 jar 系。

### 逐站明细（版本｜jar 定稿 sha｜数据包 pack｜函数目录｜平台）

| 版本 | Fabric sha | pack | 目录 | NeoForge sha | pack | 目录 |
|---|---|---|---|---|---|---|
| 26.3 | fd3aec8c | 34 | 单 | d60ebdbe | 34 | 单 |
| 26.2 | 981969e5 | 34 | 单 | 55259d2 | 34 | 单 |
| 26.1.2 | 40faa823 | 34 | 单 | 6de7b19 | 34 | 单 |
| 26.1.1 | 40faa823 | 34 | 单 | 6de7b19 | 34 | 单 |
| 26.1 | 40faa823 | 34 | 单 | 6de7b19 | 34 | 单 |
| 1.21.11 | 15a41cbb | — | 单 | 5a6b2e7 | — | 单 |
| 1.21.10 | 579055cb | — | 复* | 819e399 | 121 | 单 |
| 1.21.9 | f5c2a101 | — | 单* | 5fcbbdc | 121 | 单 |
| 1.21.8 | de15c2d6 | — | 单 | 8e85beb | 34 | 单 |
| 1.21.7 | 4fab5182 | — | 单 | 59b5e56 | 34 | 单 |
| 1.21.6 | 34b12e5e | 34 | 单 | 83dadbb | 34 | 单 |
| 1.21.5 | e2be227f | — | 单* | 10694da | 34 | 单 |
| 1.21.4 | bd2cd257 | — | — | 0112a0c | — | — |
| 1.21.3 | bb8a7e8a | — | — | a454eb3 | 34 | 单 |
| 1.21.2 | 0c1afab8 | — | — | ec5c092 | 34 | 单 |
| 1.21.1 | fe4c8204 | 34 | — | aa9517e | 34 | 单 |
| 1.21 | fe4c8204 | 34 | — | 890c5cd | 34 | 单 |
| 1.20.6 | 0b238654 | 34 | 复 | 54eb828 | 41 | 单 |
| 1.20.5 | bd8938be | 41 | 复 | 16138c4 | 41 | 复 |
| 1.20.4 | 29ed7d2d | 18 | — | 8d6887e | 18 | 复 |
| 1.20.3 | — | — | — | 2882ba3 | 15 | 复 |
| 1.20.2 | e80183f6 | 15 | 复 | 309f7ca | 15 | 复 |
| 1.20.1 | ac802dfc | 15 | 复 | 09b93d5 | 15 | 复 |
| 1.20 | b91c49f5 | 15 | 复 | — | — | — |
| 1.19.4 | 2a7985c9 | 15 | 复 | — | — | — |
| 1.19.3 | 2a7985c9 | 15 | 复 | — | — | — |
| 1.19.2 | 06c062ce（b6be37cf→回测定稿） | 34 | 复 | — | — | — |
| 1.19.1 | 06c062ce | 15 | 复 | — | — | — |
| 1.19 | 06c062ce | 15 | 复 | — | — | — |
| 1.18.2 | 47c2ed3c | 9 | 复 | — | — | — |
| 1.18.1 | 1d296aa6 | 9 | 复 | — | — | — |
| 1.18 | 2641223b | 9 | 复 | — | — | — |
| 1.17.1 | 9d1f4b0e | 9 | 复 | — | — | — |
| 1.17 | 3c1c25f4 | 9 | 复 | — | — | — |
| 1.16.5 | ae6d36e | 6 | 复 | — | — | — |
| 1.15.2 | 0225cc3 | 5 | 复 | — | — | — |
| 1.14.4 | 987dbdc | 4 | 复 | — | — | — |

\* 单/复标注为该站登记行所述；「—」为该站未单独登记（沿用相邻站模板）。1.19.2 的 34 系早期登记，与 pack 15 线并存（见第四节）。

**要点**：
- 双平台共 33 个版本（26.1~26.3 + 1.20.1~1.21.11 全线）；NeoForge 线至 1.20.1 收官（1.20 以下 Forge 47 体系未立项）；Fabric 线下探至 1.14.4。
- 1.20.3、1.17.1、1.16.5、1.15.2、1.14.4 为独立分支；其余裸版本（1.21/1.20/1.19/1.18/1.17）由相邻分支 range/patched 覆盖。

---

## 二、CI 与产物一致性核对

### 2.1 CI 全绿证明
`gh run list --limit 300` 导出统计：**28 个分支 × 2 workflows（Build & Test + Smoke Test）最近一次运行全部 `success`（55/55）**。分支清单含全部 27 个测试分支 + main。1.19.1/1.19.3/1.18.1/1.17 等由相邻分支 jar 覆盖的版本不设独立分支，其产物来源分支同样全绿。

### 2.2 实例 jar 与 CI 产物一致性（三层核对法）
1. **md5 直比**：1.20.3 实例 jar 与 CI 产物 `689337c1` 一致。
2. **CI 产物过期补救**：1.20.5/1.20.6 最新 run artifacts 已过期（artifacts 0），改用指纹法。
3. **指纹法（实例 jar vs 分支 HEAD 源码）**，1.20.x 全线 + 1.20.5/1.20.6 类级深比对：
   | 版本 | 检查项 | 实例 jar | 分支源码 | 结论 |
   |---|---|---|---|---|
   | 1.20.1 | mods.toml loaderVersion [24,)、依赖 modId=forge、mandatory×2、mixins JAVA_17、无 [[mixins]] 块、SRG 直写 | ✓ | ✓ | 一致 |
   | 1.20.2/1.20.3 | loaderVersion [1,)、mandatory×2、mixins JAVA_17、有 [[mixins]] 块 | ✓ | ✓ | 一致 |
   | 1.20.4 | JAVA_21 + type=required（Java 21 世代正确） | ✓ | ✓ | 一致 |
   | 1.20.5/1.20.6 | neoforge.mods.toml [3,)（FML 3 代正确）；mixins JAVA_21；NeoForgeEntry.class 含 RegisterEvent 字符串 ×4；ParticleManagerMixin.class 含 tickParticleList ×1 | ✓ | ✓ | 一致 |

**结论：全部实例 jar 可溯源到对应分支最新绿 CI 产物（直接比对或字节级指纹），部署一致性无例外。**

### 2.3 测试流程分层（T1/T2）
- **T1 服务端自动化**（CI Smoke）：runServer 到 Done + 命令树断言（dispatcher 含 particleex 根节点）。
- **T2 客户端人工瘦测**（每站）：进档 → baseline/deep 函数 → errprobe 预期失败 → 粒子目视 → 日志全扫。
- 单存档 + `/op self`/`/deop self` 双身份设计；errprobe 为故意写错的负例探针，验证错误直出不掩盖。

---

## 三、跨版本代际地图（全部实机实证）

### 3.1 NeoForge/Forge FML 三代边界图

| MC | NeoForge | FML | 元数据文件 | loaderVersion | 依赖字段 | [[mixins]] 块 | 运行时命名 | 网络 API |
|---|---|---|---|---|---|---|---|---|
| 1.20.1 | 20.1.x（Forge 47 fork，**modId=forge**） | 47.2.2 | mods.toml | [24,) | mandatory=true | **不解析→MANIFEST MixinConfigs** | **SRG** | SimpleChannel |
| 1.20.2/1.20.3 | 20.2.x / 20.3.8-beta | 1.0.16 | mods.toml | [1,) | mandatory=true | 支持 | mojmap | SimpleChannel |
| 1.20.4 | 20.4.x | 2.0.17 | **mods.toml**（FML 2 代不认 neoforge.mods.toml 文件名，静默拒载零警告） | [1,) | type=required | 支持 | mojmap | RegisterPayloadHandlerEvent |
| 1.20.5/1.20.6 | 3.0.x | FML 3 代 | neoforge.mods.toml | [3,) | type=required | 支持 | mojmap | 同上（`${version}` 占位符 FML 3 代起支持） |

**九条关键实证**：
1. FML 2 代不认 `neoforge.mods.toml` 文件名 → jar 被静默跳过、零警告（字节码 0 次出现该文件名）。
2. `${version}` 占位符是 FML 3 代特性 → 1.20.4 上炸 `Illegal version number`。
3. FML 1.0.16 只认 `mandatory=true` → 1.20.2/1.20.3 上 `Missing required field mandatory`。
4. **JAVA_21 mixins.json 在 Java 17 世代 = mixin 扫描期无声死亡**：无 crash report、无 debug.log、日志止于扫描——1.20.1~1.20.3 全中招（fabric 侧同款当年已修，neoforge 模块漏网两次）。
5. 依赖 modId 在 1.20.1 是 `forge`（官方 1.20.1 mods.toml + 20.2 Changelog "renamed the forge mod id to neoforge" 双核实）。
6. **参数类型注册必须 RegisterEvent**（1.21.x 全线 + backport 8/8 复查）：字段直塞在 26.x 客户端 sync 快照重建时丢条目 → 进档崩；1.21.5+ 侥幸、26.x 协议安全。1.20.1 Forge 47 特化：无 getRegistry() 用 getVanillaRegistry + `IForgeRegistry.register(RL,V)`（vanilla 注册窗口已锁，字节码实证描述符）。
7. **1.20.1 SRG 直写边界**：mixin 类内所有运行时按名匹配元素（@Inject/@Redirect 的 method+target、@Accessor 值、**@Shadow 字段名+方法名**——后者曾误判编译期处理）直写 SRG 号；Java 源码其余部分编译期 remap 即可。SRG 号由官方 mappings 双链推导（mojmap→obf→SRG），各版本不能互用。
8. 网络 API 三代：SimpleChannel（≤20.3，MessageConsumer SAM + consumerMainThread——`consumer` 是字段非方法，字节码终判）→ RegisterPayloadHandlerEvent（20.4~20.6）→ payload 注册表（1.21+）。
9. 1.20.3 是**代差错位重灾区**：名义 20.3.x，实为 FML 1.0.16 + Java 17 世代——基于 1.20.4 建分支后 mandatory/JAVA_17/网络 API 三处回退才通过；构建需 NG7 userdev（7.0.192）+ 2024-12 重建版 neoform（1.20.3-20241215.201103，才有 moddev 2.x 要的 neoform-dependencies capability）。

### 3.2 Fabric 线代际要点

| 版本段 | 关键点 |
|---|---|
| 26.x | 参数注册 RegisterEvent 化；26.1/26.1.1 NeoForge 专属 mixinextras 0.5.3 @Redirect CCE（jij 缓存换 0.5.5 修复）；函数目录单数 |
| 1.21.11 | **函数目录改单数 `function/`**（双目录并放定位法实证；早期记忆「1.21.x 一贯复数」被推翻） |
| 1.21.9/1.21.10 | pack 121；1.21.10 单数目录单放实证 |
| 1.21.x 线 | pack 34；1.21 与 1.21.1 同 jar 系（intermediary 恒定）；IBillboardParticleMixin @Mixin target 1.21.8 起由 SingleQuadParticle 改 Particle |
| 1.20.x | pack 15/18/41 分界；1.20.5/1.20.6 line pack 41 |
| 1.19.4 | **≠ 1.19.2 jar 系**（ParticleArgument.particle() 加 CommandBuildContext，1.19.2 jar 在 1.19.4 NoSuchMethodError）→ 独立分支；1.19.3 与 1.19.4 同系 |
| 1.18.x/1.17.x | pack 9；fabric-api 旧聚合 id=fabric（1.18.x 只能写旧 id，1.19.1+ 写 fabric 两端通吃）；1.17 Java 16、mixins JAVA_17 |
| 1.16.5~1.14.4 | **Java 8 降级方案**（CodeGen defineHiddenClass→ClassLoader.defineClass、record→class+accessor、mixins JAVA_8、toolchain8）——1.16.5 站 6 轮 CI 打通后 1.15.2/1.14.4 直接继承 |

### 3.3 数据包速查表

| MC 线 | pack_format | 函数目录 | 斜杠前缀 |
|---|---|---|---|
| 26.x | 34+ | function/ 单数 | 同 1.21.x |
| 1.21.9/1.21.10 | 121 | function/ 单数 | — |
| 1.21~1.21.8 | 34 | **1.21.11 起单数**，此前 functions/ 复数 | 1.21.x 无斜杠前缀 |
| 1.20.5/1.20.6 | 41 | functions/ 复数 | — |
| 1.20.4 | 18 | functions/ 复数 | — |
| 1.20~1.20.3 | 15 | functions/ 复数 | 1.19.x 有斜杠 |
| 1.17.x/1.18.x | 9 | functions/ 复数 | — |
| 1.16.5 | 6 | functions/ 复数 | 无斜杠 |
| 1.15.2 | 5 | functions/ 复数 | 无斜杠 |
| 1.13~1.14.4 | 4 | functions/ 复数 | 无斜杠 |

---

## 四、方法论沉淀（可复用结论）

1. **排查口诀**：进档成功但命令不存在 → jar 没被加载（查 `Found mod file` 行）；启动即无声死（无 crash report）→ 查 mixins compatibilityLevel vs JVM 世代；有 crash report → 看 InvalidModFileException 字段名。
2. **SRG 直写法**（1.20.1 及更早 Forge 47 世代）：mixin 类内运行时按名匹配元素直写 SRG；号源 = `client-mappings.txt`（mojmap→obf）+ `mcp_config merged tsrg2`（obf→SRG）双链推导。
3. **RegisterEvent 模式**（参数类型注册唯一正解）：事件回调内按 registry key 过滤 → 注册表写入；1.20.1 需 IForgeRegistry.register(RL,V) 特化。
4. **代差先验**：NeoForge 版本号 ≠ FML 代数（20.3.8-beta 实为 FML 1.0.16 + Java 17）；新版本先查 FML 代、Java 世代、元数据文件名、mixins compatibilityLevel 四件套再动手。
5. **Java 8 引擎降级全套**（1.16.5 模板）：语法降级（record/switch 表达式/instanceof pattern）+ CodeGen defineClass 降级 + mixins JAVA_8 + slf4j jij——向下派生新版本首轮 CI 即可双绿。
6. **双目录并放定位法**：函数目录单复数存疑时，`function/` + `functions/` 并放，看哪边被加载。
7. **函数内 sendSuccess 静默**（26.3 实证）：`/function` 内部所有命令输出被静默（含 vanilla），`say` 正常——批量链路主判据是粒子目视，不是聊天回显。

## 五、遗留与豁免记录

- **噪音豁免**（判定不影响）：Realms 401、UBO resize、xaero 超时、在线数据过期。
- **26.3 新建世界 NPE**（Holder$Reference.bindComponents null）：疑似 26.3.0.64-beta 组件绑定 + 全局数据包组合问题，与 mod 无关，未复查。
- **1.20.1~1.20.6 NeoForge 无官方 iris/sodium**（生态空缺，1.20.1 可手装 iris-forge/Oculus），粒子走 vanilla 渲染路径不受影响。
- **潜在扩展方向（未立项）**：1.19.x 及以下 neoforge/Forge 40 系；1.13.2/1.12.2 已定案放弃。

## 六、产物与归档

- 进度表明细：[test-progress.md](test-progress.md)（git 忽略，`git add -f` 强制入库）
- 实例 jar：`test/.minecraft/versions/<ver>-{Fabric,NeoForge}/mods/particleex-*-{fabric,neoforge}-<ver>-1.0.0.jar`
- CI 导出包：`test/ci-<ver>/`（gh run download 解包）
- 数据包模板：acb-test-1.20.2（pack 15，1.20.1~1.20.3 共用）/ acb-test-1.20.4（pack 18）/ acb-test-1.20.6（pack 41）/ 低版本线各自 pack 4~9
