# AnotherColorBlock 发布前人工回归清单

> 用途:**每个 MC 版本 / 平台分支发布前的最终人工回归**。
> 本清单是发布闸门:全部勾选通过才允许打 tag/发布。
> 环境:按 `docs/testing/smoke-test.md` 第 1 节准备(**独立服务端 + 独立客户端**,
> 两端同版本);命令可直接复制,预期与排查见 smoke-test.md 对应小节。
> 使用:每完成一项把 `[ ]` 改为 `[x]`,并在旁注明测试环境
> (例:`[x] 1.21.11 / Fabric / 服务端+客户端分离 / 2026-10-09`)。

---

## 0. 构建产物与双端环境

- [ ] `./gradlew :fabric:build` 与 `:neoforge:build` 均成功,产物在 `build/libs/`
- [ ] jar 内 `fabric.mod.json` / `neoforge.mods.toml` 版本号与发布版本一致,
      `depends.minecraft` 为 `1.21.11`(fabric 侧为精确等号 `=1.21.11`)
- [ ] 服务端与客户端各装一份,jar 哈希一致(同一构建)
- [ ] 服务端 `eula=true`;测试用单人存档开启作弊(或服务器 `/op` 测试账号)
- [ ] 图片 `particleImages/logo.png`、视频 `particleVideos/demo.mp4`、
      javacv 四件套均就位于**客户端**运行目录(如测视频)

## 1. 表达式引擎

- [ ] 基础运算:`parameter ... "x,y,z=t,sin(t),0"` 正弦波曲线形状正确
- [ ] 多变量一次赋值(逗号式)与多语句分号式:
      `"x,y,z=t,sin(t),0"`(一次赋值三分量)、`"x=t;y=t^2"` 均能解析执行
- [ ] 运算符优先级与幂运算:`^` 按指数解析(`sin(t)^3` 爱心案例形状对)
- [ ] 比较/逻辑:`conditional` 球壳与甜甜圈案例(含 `==`、`<`、`&`、`|` 用法)判定正确
- [ ] 常量 `PI`、`E`(如 `cos(t*PI/2)` 在预期位置取极值)
- [ ] 函数集:抽查 `atan2`、`random`、`pow`、`floor`、`max/min` 各一例,结果合理
- [ ] 非法表达式(乱写如 `"x,,y=t"`、括号不配对)给出错误反馈,客户端**不崩溃**
- [ ] 除零/NaN 输入(如 `"y=1/x"` 在 x=0 附近):不崩、不造成粒子行为错乱
- [ ] 速度表达式变量 `t` 按 speedStep 累加(轨道案例节拍近似匀速)
- [ ] 变量全集可用性:`x,y,z,s1,s2,dis,t,vx,vy,vz,cr,cg,cb,alpha,cx,cy,cz,
      dx,dy,dz,ds1,ds2,ddis,age,destroy` — 用 `group change parameter`
      `"x,y,z=s1,s2,dis"` 类互相赋值抽查 3 个以上

## 2. 命令解析(Brigadier 行为)

- [ ] 11 个子命令全部注册:`normal / conditional / parameter(8 变体)/
      image / imagematrix / video / videomatrix / group remove / group change /
      clearparticle / clearcache / functionlist`
- [ ] 每个子命令**省略全部可选参数**时可用语法提示(补全候选合理)
- [ ] 参数类型校验:颜色 4 个数、速度/范围各 3 个数、`flip` 枚举、旋转 90 倍数
      ——错误输入弹红色错误提示且无副作用
- [ ] 表达式字符串带引号/带空格均正常;不带引号的裸 `t` 也能解析(单 token 情形)
- [ ] 相对坐标 `~` 与绝对坐标等价(同点生成图案重合)
- [ ] 权限:无权限账号执行 `particleex` 提示无权/未知命令;`/op` 后可用
- [ ] 命令补全:输入 `/particleex` + Tab 提示完整(含变体);group 后 Tab 提示
      `remove/change`;`functionlist` 输出内容与完整函数清单一致

## 3. 网络收发(payload)

- [ ] 服务端执行命令 → 客户端能收到并渲染(11 种 payload 的**正向**路径各 1 例:
      ClearParticle / ClearCache / Normal / Conditional / Parameter / Image /
      ImageMatrix / Video / VideoMatrix / GroupRemove / GroupChange)
- [ ] 双端 jar 同版本时**无** payload 解码警告(`Failed to decode packet` 之类)
- [ ] RCON/控制台执行构包的 payload(服务端 encode 路径)不产生异常
- [ ] **b904f4c 回归-场景 B**:独立服务端执行发粒子命令,客户端连服后
      **不被踢、不断线**,服务端日志无 `ClassCastException`
- [ ] **b904f4c 回归-场景 C**:全新客户端启动无
      `Packet type already registered` 报错
- [ ] 多客户端并存时,命令对所有在线客户端广播生效(2 个客户端互验)
- [ ] 玩家中途加入(粒子已存在):加入者无异常(新玩家本地不会凭空出现旧粒子——
      属预期,本模组粒子为客户端本地管理)

## 4. Mixin 注入(客户端)

- [ ] 日志无 `mixin apply failed` / `injection failed` / refmap 错误
- [ ] 颜色注入:`normal` 指定任意颜色,粒子颜色一致(白=full white 是注入失败)
- [ ] 寿命注入:age=200 粒子 10 秒准时消散;age=-1 永久存在(用 clearparticle 清掉)
- [ ] tick 注入:速度表达式(轨道/渐变/destroy)逐帧生效
- [ ] `ParticleManager` 注入:粒子总数受控(1.2.12 的 7 万案例不失控)
- [ ] 材质/varia:图片粒子与普通粒子同屏无渲染冲突(混合渲染不闪不花)
- [ ] **NeoForge 特有**:`neoforge.mods.toml` `[[mixins]]` 生效(在
      smoke-test 3.0 的启动头检查,Mixin 列表与 fabric 侧 5 个一致)

## 5. 分组(group)

- [ ] 创建:带 `group=heart` 生成粒子,后续命令对 heart 组生效
- [ ] 条件删除:`group remove heart "age>100"` 只删满足条件的
- [ ] 无条件删除:`group remove heart` 全组清空;已不存在的组执行不报错
- [ ] 改属性:`group change parameter heart "cr,cg,cb=0,1,0"` 变色生效
- [ ] 改速度:`group change speedexpression heart "vx,vz=-z*0.1,x*0.1"` 旋转生效
- [ ] 条件修改:`group change speedexpression heart "vy=0.1" "age>50"`
      只对部分粒子生效
- [ ] 位置参数:`group remove`/`group change` 的 `pos` 缺省用玩家位置、
      显式给出绝对坐标时以给定位置为基准(相对 `x,y,z` 计算正确)
- [ ] 多组 `a|b`:加入时同时入两组的粒子,任一组的 remove 都对其生效(并集语义)

## 6. 图片 / 视频

- [ ] `image` 用 `logo.png`(64×64 自备图)显示,形状/方向正确
- [ ] 缩放(0.5 / 0.1)与旋转(90/180/270)/翻转(horizontally / vertically)组合正确
- [ ] `imagematrix`:`E3`/`E4` 与自定义矩阵(下移 100 格示例)行为正确
- [ ] 图片替换后 `clearcache` 生效立即显示新图
- [ ] 非法路径(不存在文件、`../` 越界)给出错误且不崩
- [ ] 视频(可选,需 javacv):`video` 逐帧播放、`videomatrix` 同矩阵验证;
      javacv 缺失时优雅报错不崩(客户端日志可定位)

## 7. 配置

- [ ] `config/particleex.json` 首启自动生成,字段为
      `maxParticleCount=65536`、`ParallelParticleUpdate=false`
- [ ] 改 `maxParticleCount`(如 1000)后重启生效:超量生成被截断
- [ ] `ParallelParticleUpdate=true` 下重复第 1~6 节核心案例,行为与串行一致
- [ ] 删除配置文件后模组按默认值重建,不崩
- [ ] 配置文件 JSON 损坏时:启动报错信息可读(不静默吞掉)

## 8. 双端兼容(Fabric ↔ NeoForge)

- [ ] Fabric 客户端 + Fabric 服务端:全量冒烟通过
      (smoke-test.md 第 2 节)
- [ ] NeoForge 客户端 + NeoForge 服务端:全量冒烟通过
      (smoke-test.md 第 3 节)
- [ ] 独立服务端发包回归(b904f4c):Fabric 与 NeoForge **各跑一遍**
      smoke-test.md 4.2,均不断线
- [ ] 双端版本一致性负面用例:版本故意不一致时报错清晰、无静默异常
- [ ] 单人模式(自洽双端)冒烟:功能与联机一致
- [ ] 改造回归关注点:common 代码被两平台同时编译后,命令树/参数类型/表达式
      行为在两端完全一致(对比执行同一命令的图案)

## 9. 稳定性与性能(建议全量,至少抽样)

- [ ] 7 万粒子上限案例不崩、可恢复(ParticleManager 注入 + 上限回落)
- [ ] 连续执行 20 条大命令(conditional 球壳 ×5)后帧率恢复,无粒子泄漏
- [ ] `clearparticle` 后粒子数归零(不信眼力可用 F3 或 modmenu 类工具观察)
- [ ] 挂机 10 分钟无内存线性增长迹象、日志无持续报错

---

## 记录

| 版本分支 | 平台 | 日期 | 执行人 | 结果 | 备注 |
|---|---|---|---|---|---|
| 1.21.11 | Fabric | | | | |
| 1.21.11 | NeoForge | | | | |
| (后每版本分支一行) | | | | | |

> 全部 `[x]` 且无遗留备注项 → 允许打 tag 发布;
> 任一 `[ ]` 存在时,发布负责人需在小节备注写明豁免理由并签字。