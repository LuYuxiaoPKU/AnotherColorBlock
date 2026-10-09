# AnotherColorBlock 游戏内冒烟测试指南

> 适用分支:1.21.11(multiloader 改造中,common / fabric / neoforge 三模块)
> 适用平台:Fabric 与 NeoForge(各独立成章)
> 架构前提:**服务端指令 + 客户端渲染**。所有命令以 `/particleex` 为根,
> 需要**权限等级 2**;服务端只负责指令解析与下发网络包,粒子的计算与渲染全部在客户端完成。

本文档是本模组**实机(游戏内)冒烟测试**的正式规程,与《如何造假造一个
游戏内测试体系》无关——以下每一步都有可观察、可判定的"预期结果"。
配套文件:`test/commands.txt`(可直接复制的命令合集)、
`docs/testing/gametest-ci.md`(CI 自动化设计)、`docs/testing/manual-checklist.md`(发布前回归清单)。

> 说明:多平台改造尚未完成、本机暂无法构建时,本文档作为发布前测试规程留存;
> 文档内的命令语法均已对照 `common/src/main/java/com/noone/particleex/command/particlep/`
> 源码逐一核对(含对 `docs/particle-cases.md` 两处缺参笔误的修正,见 2.5/2.7 注)。

---

## 0. 必读:测试前置知识

1. **权限**:`/particleex` 全部子命令要求权限等级 2(源码:
   `Permission.Level(PermissionLevel.GAMEMASTERS)`)。单人游戏需开启"允许作弊",
   多人服务器需先 `/op <玩家>`;用服务器控制台(console)执行天然拥有该权限,常用于双端验证。
2. **双端都装**:多人联机时服务器与**每个玩家客户端**都必须安装本模组
   (且版本一致)。单人游戏装一份即可(单机同时承担两端)。
3. **无默认反馈**:多数命令执行成功时**不输出任何聊天消息**(命令树直接返回成功)。
   看到"无任何反应"不一定是失败,按本文档各条目的"预期结果"观察粒子本身。
4. **age(寿命)语义**:`-1` 永久存活;`0` 使用默认寿命(约 2~3 秒);
   `>0` 指定存活 tick 数(20 tick = 1 秒)。冒烟测试建议统一用 `200`(10 秒),
   给观察留足时间;避免冒烟时用 `-1` 造成粒子堆积。
5. **表达式字符串必须带引号**且含空格时整体引用,复制命令时连同引号一起粘贴。
6. **失败排查速查表**见第 5 节;双端 b904f4c 回归见第 4 节。

---

## 1. 环境准备

### 1.1 概念:游戏目录与 mods

- **客户端游戏目录**:官方启动器为 `.minecraft/`(版本隔离时注意 profile 的实际目录)。
- **服务端运行目录**:服务端 jar 所在目录。
- 两端都要把模组 jar 放进 `mods/` 文件夹(没有就新建)。
- 模组 jar 在构建产物 `build/libs/` 下,按当前命名规则为:
  - `particleex-fabric-1.21.11-<version>.jar`(fabric 模块)
  - `particleex-neoforge-1.21.11-<version>.jar`(neoforge 模块)
  - 实际文件名以构建产物为准,注意**版本分支必须与游戏版本一致**(1.21.11)。

### 1.2 Fabric 客户端安装(简述)

1. 下载 Fabric Installer(<https://fabricmc.net/use/>),选择 Minecraft 1.21.11、
   Loader 建议 0.18.x(模组依赖 `fabricloader >= 0.17.0`),安装到官方启动器。
2. 启动器新建的 `fabric-loader-1.21.11` profile 启动一次(生成运行目录)。
3. 把以下 jar 放入该 profile 的 `mods/`:
   - `particleex-fabric-1.21.11-<version>.jar`
   - Fabric API(1.21.11 对应版本,项目构建使用 `0.141.6+1.21.11`;模组依赖 `"fabric": "*"`)
4. 启动游戏并进入存档(单人世界需开启"允许作弊")。

### 1.3 Fabric 独立服务端安装(简述)

1. 准备目录 `server/`,下载 Minecraft 官方 `minecraft_server.1.21.11.jar`。
2. 下载 Fabric Installer,执行服务端模式:
   `java -jar fabric-installer.jar server -mcversion 1.21.11 -loader 0.18.4 -dir server`
3. 先启动一次生成配置,修改 `eula.txt` 为 `eula=true`,并按需改 `server.properties`
   （联机测试建议 `online-mode=false` 便于本地互连;开 RCON 用于自动化,见 gametest-ci.md）。
4. 把 `particleex-fabric-*.jar` **与 Fabric API jar** 放入 `server/mods/`。
5. `java -jar fabric-server-launch.jar nogui` 启动。
6. 客户端通过 `Multiplayer` 用 `localhost` 连接(客户端需满足 1.2 的安装)。

> 注意:独立服务端**不需要**放置图片/视频资源(渲染在客户端,见 1.6);
> 但服务端必须有模组,否则不存在 `/particleex` 命令。

### 1.4 NeoForge 客户端安装(简述)

1. 从 neoforged.net 下载 NeoForge 21.11.0 的 installer,双击安装到官方启动器。
2. 启动新建的 NeoForge profile 一次(生成运行目录),把
   `particleex-neoforge-1.21.11-<version>.jar` 放入 `mods/`。
3. NeoForge **不需要**独立 API 依赖(Fabric API 无需安装)。

### 1.5 NeoForge 独立服务端安装(简述)

1. 下载 NeoForge 21.11.0 server 安装包,按向导安装到 `server/` 目录。
2. 首次运行生成配置后,`eula.txt` 置 `eula=true`,启动 `run.sh`(Windows 用 `run.bat`)。
3. 把 `particleex-neoforge-*.jar` 放入 `server/mods/`,重启。

### 1.6 图片 / 视频 / javacv 资源放置(注意:放在**客户端**运行目录)

渲染在客户端完成,所以资源必须存在于**每个客户端**的运行目录:

| 资源 | 必须目录(相对客户端运行目录) | 说明 |
|---|---|---|
| 图片 | `particleImages/` | `image/imagematrix` 命令读取(源码 `ImageUtil` 固定 `./particleImages`) |
| 视频 | `particleVideos/` | `video/videomatrix` 命令读取 |
| javacv 库 | `javacv/` | 视频功能运行时加载,见下 |

javacv 四个 jar(javacv / javacpp / ffmpeg / ffmpeg-platform,版本需与构建依赖匹配,
当前 gradle 依赖 `javacv:1.5.11`,README 示例为 1.5.10,请以实际构建依赖版本为准),
从 Maven Central 获取后放入 `javacv/`,客户端**重启**后生效。

> 独立服务端目录下不需要上述资源;但 **javacv 与图片缺失时客户端不崩溃**,
> 仅表现为粒子不显示或日志报错,判断方法见 5.4 / 5.5。

### 1.7 配置文件

- 首次启动自动生成 `config/particleex.json`(Fabric 在游戏目录 `config/`,NeoForge 同理,通过平台 path 注入)。
- 字段:`maxParticleCount`(默认 65536,模组生成粒子上限)、
  `ParallelParticleUpdate`(默认 false,并行更新粒子,开启后若出现粒子抖动/时序异常即为回归)。
- 冒烟测试保持默认值;若测试中颗粒子**凭空消失/生成被截断**,检查是否触及上限。

---

## 2. Fabric 冒烟清单

> 进入一个开阔地段(建议 `~50 50 50` 附近),面向正前方。以下命令均可在聊天框
> 或服务器控制台执行;控制台执行时位置用 `~ ~ ~` 会退化为世界原点,请改用绝对坐标。
> 每小节格式:验证命令 → 预期结果 → 常见失败排查。

### 2.1 基础:命令树注册、权限与 functionlist 输出

```
/particleex functionlist
```

- 预期:聊天栏输出 `sin(a), cos(a), ...` 完整函数列表(源码 `FunctionListCommand`
  内置常量串,含 `sin(a)` 起头、`powerOfTwoD(n)` 收尾)。**本条是"命令树已注册、
  权限已满足"的最快判定**——若连它都报"未知命令",模组未加载或权限不足。
- 排查:
  - `Unknown command`(Brigadier 红色提示):服务端未加载模组,
    或执行者权限等级 < 2。单人请检查是否开启作弊;服务器请 `/op`。
  - 服务端日志 `particleex` 相关 `Exception`、`Mod resolution` 失败:
    模组依赖缺失(Fabric API 未装 / Loader 版本 < 0.17.0)。

### 2.2 normal — 普通粒子(最小可用性)

```
/particleex normal minecraft:heart ~ ~2 ~ 1 0 1 1 0 0 0 0 5 200
```

(语法:`normal <粒子> <位置> <颜色> <速度> <范围> <数量> [age] ...`;
上例:位置上方 2 格、粉红色、静止、无偏移、5 颗、寿命 200 tick)

- 预期:5 颗粉色爱心在指定点附近生成,原地停留约 10 秒后消散;颜色为粉红
  (与 `1 0 1 1` 一致)。
- 排查:
  - 出现但**颜色是白的** → 客户端 Mixin 未生效(`IBillboardParticleMixin` /
    `IParticleMixin` 未注入 setColor/setAlpha),见 5.1。
  - 出现后**立即消失/不受 age 控制** → `ParticleMixin` 寿命注入未生效,见 5.1。
  - 完全无粒子但命令不报错 → payload 未送到客户端,见 5.2。

### 2.3 parameter — 爱心参数方程

```
/particleex parameter minecraft:heart ~ ~1.5 ~ 1 0 1 1 0 0 0 0 6.28318 "x,y,z=1.6*sin(t)^3,1.8*cos(t)-0.6*cos(2*t)-0.2*cos(3*t)-0.1*cos(4*t),0" 0.02 200
```

- 预期:生成**竖立爱心**轮廓(宽约 1.6、高约 1.8),粉色,寿命 200 tick(10 秒),
  逐点成型过程平滑。这是参数方程(表达式引擎 + `parameter` 命令 + payload)的复合验证。
- 排查:
  - 报参数错误(红色提示):表达式未加引号,或 `^`/`t` 大小写错误。
  - 命令成功但**没有形状、只有零散点** → 表达式引擎计算结果异常
    (如 `sin(t)^3` 解析失败退化为常量),见 5.1 的日志检查;或 payload 丢失,见 5.2。

### 2.4 polarparameter — 极坐标螺旋

```
/particleex polarparameter minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 0 100 "s1,s2,dis=t/5,t/50,1" 0.1 200
```

- 预期:`end_rod` 粒子沿**球坐标螺旋**分布:水平角 `t/5`、仰角 `t/50`、半径 1,
  形成一条缠绕球面的上升曲线(对比:平铺正圆应是 `s2=const`)。
- 排查:
  - 展开成**平面圆**而非 3D 曲线 → 球坐标换算(`dis·cos(s2)·cos(s1)` 等)在
    客户端异常,检查 `ParticleStruct` 的 s1/s2/dis → x/y/z 换算路径,或 `s2` 表达式被简化。
  - 命令报 `Expected double` 类错误 → 参数个数不对(此命令 10 位固定数字参数)。

### 2.5 conditional — 球壳

> 注:`docs/particle-cases.md` 1.4 原案例缺 `range` 三个数字(位置偏移后的 3 个范围参数),
> 按源码 `ConditionalCommand` 已核正为下面的写法(grep 亦可验证)。

```
/particleex conditional minecraft:end_rod ~ ~3 ~ 1 1 1 1 0 0 0 1 1 1 "abs(x*x+y*y+z*z-1)<0.05" 0.08 200
```

(语法:`conditional <粒子> <位置> <颜色> <速度> <范围> <表达式> [step] [age] ...`;
范围 `1 1 1` 表示采样半范围 ±1,step 0.08 表示沿每个轴步进采样)

- 预期:半径 1 的**球壳**由粒子构成,壳厚约 0.1,完整包住中心点,无实心填充。
  采样点约 1.5 万,生成可能瞬间掉帧属正常。
- 排查:
  - **整个方块实心** → 条件表达式恒真(如比较运算符 `>` 被解析为不认识的符号而整式
    被当作真),用 `functionlist` 对照运算符支持;
    或表达式被错误整体求值,检查 `Parser` 对 `==`/`!=` 的识别。
  - 完全无粒子 → 见 5.2(payload)或 5.1(Mixin)。

### 2.6 conditional — 甜甜圈(环面)

```
/particleex conditional minecraft:end_rod ~ ~3 ~ 1 1 1 1 0 0 0 1.5 0.5 1.5 "abs(sqrt(x*x+z*z)-1.5)^2+y*y<0.16" 0.08 200
```

- 预期:**环面**(甜甜圈)图案,主半径 1.5、管半径 0.4,中间空心;表达式中的 `^2`
  为幂运算(不是位异或)。采样范围需能覆盖管道半径(故 y 方向用 0.5)。
- 排查:
  - 图案变成**两个环/碎片** → 采样半范围覆盖不足(减小范围保证表达式变量全域可取值)
    或 step 过大导致采样点不连续;`abs(sqrt(x*x+z*z)-1.5)` 在 `x²+z²=0` 处为 −1.5,
    平方后恒 < 0.16 是正常的外管逻辑,勿"修"它。
  - 生成时报错或卡死数秒 → 采样点数爆表(范围/step 大了约 7.6 万点会明显卡顿),
    减小范围或调大 step 到 0.1。

### 2.7 image / imagematrix — 图片粒子(需自备图片)

> 前置:任意 png/jpg 放入**客户端运行目录**的 `particleImages/`(见 1.6),
> 例如制作一张 64×64 的 `logo.png`。路径为相对 `particleImages/` 的文件名。

```
/particleex image minecraft:end_rod ~ ~2 ~ logo.png 0.5 0 0 0 not 10 0 0 0 200
```

(语法:`image <粒子> <位置> <路径> [缩放] [xRotate] [yRotate] [zRotate] [翻转] [dpb] [速度] [age] ...`;
缩放 0.5、不旋转、不翻转、dpb=10、速度 0 0 0、寿命 200 tick。注意 `age` 必须
排在速度(3 个数)之后——这也是 cases 案例不带 age 能执行、直接跟 age 会报参数错的原因)

- 预期:以 `end_rod` 粒子逐像素还原 `logo.png`,图像约 0.5 倍大小、方向正确
  (旋转 0 时图片"正着"显示),停留 10 秒。
- 排查:
  - 无任何粒子 + 日志 `IOException`/`invalid image path` → 图片不在
    `particleImages/` 或文件名/大小写不匹配(路径做了 canonical 校验,防目录穿越,
    含 `..` 会被拒)。
  - 显示为**旧图** → 替换图片后需 `/particleex clearcache`(见 2.10)。
  - 图片方向左右/上下颠倒 → 检查 `flip` 参数(`not` / `horizontally` / `vertically`)。

翻转与矩阵验证(imagematrix,矩阵 `E3`=恒等):
```
/particleex imagematrix minecraft:end_rod ~ ~2 ~ logo.png 0.5 E3 10 0 0 0 200
```

- 预期:与 `image` 相同图案(恒等矩阵,E3 为等价写法);
  传入自定义矩阵如 `"(1,0,0,0,,0,1,0,0,,0,0,1,-100,,0,0,0,1)"` 时应上下平移 100 格
  或收到参数错误(行列 `,,` 分隔语法)。
- 排查:矩阵字符串引号/`,` 格式错误 → 命令参数解析失败(红色提示),检查 `,,` 分隔。

### 2.8 video / videomatrix — 视频粒子(可选,需 javacv)

> 前置:mp4 放入客户端运行目录 `particleVideos/`,javacv 四个 jar 放入 `javacv/`(见 1.6)。
> 视频解码需 FFmpeg 平台包,缺失时**命令本身可执行,但客户端无粒子**。

```
/particleex video minecraft:end_rod ~ ~2 ~ demo.mp4 0.2 0 0 0 not 10 0 0 0 200
```

- 预期:粒子按视频逐帧显示(~0.2 倍尺寸),画面连续播放。
- 排查:
  - 无粒子 + 客户端日志
    `NoClassDefFoundError`/`ClassNotFoundException: org.bytedeco.*` → `javacv/`
    目录缺失或不完整(需完整 4 个 jar 且版本配套)。
  - 只有**第一帧/静态图** → FFmpeg 平台库与系统不匹配(换 `ffmpeg-platform` 对应平台包)。
  - 视频正常但很卡 → dpb(每方块粒子数)调小(如 5)。

### 2.9 group — 分组增删改

先创建带组的粒子(本命令同时验证"组"参数;`age=200,速度表达式=null,步长=1,组=heart`):

```
/particleex normal minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 50 200 null 1 heart
```

- 预期:50 颗 `end_rod` 粒子生成。group 为**客户端分组**,服务端命令仅转发包,
  此时无任何可见分组提示,正常。

按条件删除(移除 heart 组中存活超过 100 tick 的粒子,即 5 秒后执行):

```
/particleex group remove heart "age>100"
```

- 预期:执行后 heart 组中龄期 > 100 tick 的粒子**立即消失**,更年轻的保留。
- 排查:全组清空或全不消失 → 表达式条件变量 `age` 初始值赋值在
  `GroupUtil.remove` 中(`data.age = 粒子当前 age`),检查条件写法
  (如误用 `>` 语义反转)。

无条件删除(清空整组):

```
/particleex group remove heart
```

- 预期:heart 组所有粒子消失。

修改组属性(group change,两种 type):

```
/particleex group change speedexpression heart "vx,vz=-z*0.1,x*0.1"
/particleex group change parameter heart "cr,cg,cb=0,1,0"
```

- 预期:第一条执行后 heart 组剩余粒子**绕中心旋转**(轨道速度表达式);
  第二条将剩余粒子改**绿色**(`cr,cg,cb` 赋值 0,1,0)。
  - `group change parameter` 的表达式中可用变量为粒子**相对中心偏移**
    `x,y,z`、当前速度 `vx,vy,vz`、颜色 `cr,cg,cb,alpha`(见 2.9 源码
    `ClientNetworkHandler.groupChange` 的 data 预填)。
- 条件化修改(仅对满足条件的粒子生效):

```
/particleex group change speedexpression heart "vy=0.1" "age>50"
```

- 预期:仅让 heart 组中存活 > 50 tick 的粒子获得 `vy=0.1` 上飘速度。
- 多组语法(创建时用 `|` 加入多个组,移除时任一组成员生效):
```
/particleex normal minecraft:end_rod ~ ~1 ~ 1 1 1 1 0 0 0 10 200 null 1 a|b
/particleex group remove a
```
  预期:a|b 命令后粒子同时属于 a 与 b;移除 a 后 **b 组同样受影响(粒子已删)**——
  注意 `|` 多组是并集语义。

### 2.10 clearparticle / clearcache

```
/particleex clearparticle
```

- 预期:模组生成的所有粒子(含各分组)**瞬间全部清除**(客户端 `GroupUtil.clear()`
  + 粒子表清理)。
- 排查:清除后仍有少量粒子在游走 → 该粒子可能来自原版/其他模组;
  `clearparticle` 只清本模组管理对象。
- 与 2.9 组合:生成 → `clearparticle` → 用眼睛确认画面净空,再接后续用例。

```
/particleex clearcache
```

- 预期:无可见变化;**替换过图片后执行**,下一次 image 命令应显示新图。
- 排查:替换图片后不重载 → 忘记执行 clearcache,或图片路径与缓存 key 不完全一致。

### 2.11 表达式综合:速度表达式与渐变(轨道 / 渐隐 / destroy)

```
/particleex normal minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 20 200 "vx,vz=-z*0.1,x*0.1" 1
```

- 预期:20 颗粒子**绕原心旋转**(每 tick 执行 `vx,vz=-z*0.1,x*0.1`),10 秒后消失。
- 排查:粒子静止不动 → 速度表达式未执行(`ParticleMixin`/tick 注入失效,见 5.1),
  或表达式被整体判定常量(检查 `t` 是否可用、`*` 是否正确转义)。

```
/particleex normal minecraft:dust ~ ~2 ~ 1 0 1 1 0 0 0 40 200 "cr,cg,cb=1,1-t/200,1-t/200" 1
```

- 预期:40 颗粉色 dust 粒子,蓝绿通道随时间**线性衰减**(200 tick 内渐变),
  表现"逐渐变红"。
- 排查:颜色不变 → 颜色通道注入未生效,见 5.1 的 `IBillboardParticleMixin` 检查。

```
/particleex parameter minecraft:flame ~ ~2 ~ 1 1 1 1 0 0 0 0 60 "x,y,z=t,sin(t),0;destroy=t>40" 0.05 200
```

- 预期:正弦波曲线,**t>40 后粒子逐段消亡**(`destroy` 置非 0 立即销毁),
  剩下一段约 40/60 的曲线,其余粒子在各自 age 到期前被强制销毁。
- 排查:`destroy` 不生效 → 表达式引擎的条件分支或 destroy 通道失效,见 5.1。

### 2.12 压力与稳定性(可选,建议发布前跑)

- `maxParticleCount` 上限触发:`/particleex normal minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 70000 200`
  后无崩溃。预期:客户端日志出现数量截断提示(如有),游戏不崩溃、掉帧可恢复。
- 长时间挂机 10 分钟:确认粒子不泄漏(`F3` 观察),`clearparticle` 后粒子数归零。
- 开启 `ParallelParticleUpdate=true`(config)重复 2.3~2.9 各一次,确认行为与顺序执行一致
  (并行更新下若出现颜色错乱/粒子瞬移即为回归)。

---

## 3. NeoForge 冒烟清单

> 命令本体与 Fabric **完全相同**(命令树在 common 模块,双端共享;
> 差异仅在平台层:入口 `@Mod`、Mixin 注册于 `neoforge.mods.toml`、网络 `PacketDistributor`)。
> 因此本节只列环境差异点,用例直接复用第 2 节命令
> (推荐顺序:2.1 → 2.3 → 2.5 → 2.9 → 2.10 → 2.11,即 functionlist/爱心/球壳/分组/清理/表达式)。

### 3.0 NeoForge 特有环境检查

- 日志启动头:出现 `Loading Minecraft 1.21.11` 后应看到
  `Mod List` 中包含 `particleex`(mod id),版本号与 jar 名一致;若不在列表中,
  检查 `neoforge.mods.toml` 与 jar 放入位置。
- **Mixin 载入检查**:NeoForge 的 Mixin 注册在 `META-INF/neoforge.mods.toml` 的
  `[[mixins]]` 表(映射用 Mojang 官方名,与 common 一致)。启动日志如出现
  `mixin apply failed` / `injection failed` / `refmap` 相关 ERROR,后续所有
  "预期"将整体失效,先修 Mixin 再测。
- 版本匹配:NeoForge 21.11.0 对应 MC 1.21.11,`neoforge_loader_version_range=[4,)`;
  服务端与客户端 NeoForge 主版本必须一致(21.11.x),否则连不上。

### 3.1 ~ 3.9 功能用例

- **3.1 命令树/权限/functionlist**:执行 2.1 相同命令与预期。
- **3.2 parameter 爱心**:执行 2.3 相同命令与预期。
- **3.3 conditional 球壳**:执行 2.5 相同命令与预期。
- **3.4 group 增删改**:执行 2.9 序列(创建 → 条件删 → 改色 → 改速度)与预期。
- **3.5 清理与缓存**:执行 2.10 两条命令与预期。
- **3.6 表达式综合**:执行 2.11 三条命令与预期。
- **3.7 图片粒子**:执行 2.7 命令与预期(图片同样放**客户端** `particleImages/`)。
- **3.8 视频粒子(可选)**:执行 2.8 命令与预期(javacv 放 `javacv/`)。
- NeoForge 特有排查:
  - 命令报"未知命令"但 mod 列表有 particleex → 命令注册回调未挂上:
    检查 `RegisterCommandsEvent` 或 common 注册器在 NeoForge 侧的调用点。
  - 命令成功、粒子无 → 与 Fabric 侧同样先查 payload 链路,但注意 NeoForge 用
    `PacketDistributor.sendToPlayers`,排查 5.2 时以 NeoForge 日志与
    `ClientPayloadContext` 处理器注册为准。

---

## 4. 双端安装验证(含 b904f4c 回归)

> **背景**:提交 `b904f4c`("fix: 在 main 入口注册 clientbound payload,
> 修复独立服务端发粒子包客户端断线")修复了两处问题:
> ① 11 个 clientbound payload 原只在客户端入口 `ParticleExClient` 注册,
> 独立服务端编码发包时抛 `ClassCastException`(未注册为 CustomPacketPayload),
> 玩家被踢下线;② 客户端入口同时存在重复注册风险(Packet type already registered)。
> 修复后改在 `ParticleEx.onInitialize()`(main 入口,双端均执行)统一注册,
> 客户端入口移除重复注册。**每个平台、每次发布都必须在独立双端环境复测以下场景。**

### 4.1 场景 A:标准双端联机(正向功能)

1. 按 1.2/1.3(或 1.4/1.5)准备好**独立**服务端与客户端,模组版本一致。
2. 客户端 `Multiplayer` 连接 `localhost`,进入世界。
3. 客户端执行 `/particleex parameter ...`(2.3 爱心命令)。
   - 预期:服务端日志无异常;客户端正常渲染爱心;**玩家在线状态稳定,无踢出/断线**。

### 4.2 场景 B:独立服务端发粒子包回归(b904f4c 主场景)

1. 服务端控制台(console,天然权限 2)执行:
   `/particleex parameter minecraft:heart 0 8 0 1 0 1 1 0 0 0 0 6.28318 "x,y,z=1.6*sin(t)^3,1.8*cos(t)-0.6*cos(2*t)-0.2*cos(3*t)-0.1*cos(4*t),0" 0.02 200`
   (注意:控制台无玩家位置,`~` 会退化,请用绝对坐标 `0 8 0`)
2. 轮流执行本清单中其余命令(normal / conditional / group / clearparticle 等)各一条。
   - 预期:
     - 服务端日志**无** `ClassCastException` / `Exception` / `ERROR`;
     - 客户端玩家**不被踢出**,不掉线;
     - 客户端画面正常出现对应粒子(b904f4c 修复前的表现:
       `ClassCastException: class ...Payload cannot be cast to class CustomPacketPayload`,
       玩家立刻 "Connection lost" / 被踢)。
   - 失败判定:出现上述任一异常 → 回归失败,禁止发布。
3. group 类命令的观察点:服务端经 `PlayerLookup.world` 全玩家广播,
   所有在线客户端都应收到(多客户端时可各端观察)。

### 4.3 场景 C:客户端启动回归(payload 不重复注册)

1. 全新客户端环境按 1.2/1.4 安装模组(注意与 4.2 用不同运行目录,排除残留)。
2. 启动客户端。
   - 预期:正常进入主菜单,日志**无** `Packet type already registered` /
     `Cannot register` 类错误(b904f4c 修复前的表现:客户端启动即崩溃)。
3. 进入世界执行任意一条命令,再次确认渲染正常。

### 4.4 双端版本一致性检查

- 两端 jar 必须**同一分支同一提交**产物:比对 `mods/` 下 jar 的哈希
  (或对照 `fabric.mod.json` / `neoforge.mods.toml` 的版本号与构建时间)。
- 两端 Fabric 侧还需一致:Loader 版本(≥ 0.17.0,建议 0.18.x)、Fabric API 版本
  (建议与构建用 0.141.6+1.21.11 同一 API 版本线)。
- 预期:客户端连接成功;若版本不一致,常见表现为 5.3 的症状。
- singleplayer 视为"双端同版"的自洽环境,可做 2.x 全量冒烟,但不能替代场景 B。

---

## 5. 常见失败排查速查表

| 症状 | 最可能原因 | 检查点 | 处置 |
|---|---|---|---|
| 命令报"未知命令" | 服务端未装模组 / 权限不足 | 服务端日志 Mod List;`/op` 或单人开作弊 | 补装模组;提权 |
| 粒子为**白色**或颜色参数无效 | 颜色注入 Mixin 未生效 | 客户端日志 `Mixin apply failed`/`injection failed`/refmap(5.1) | 查 mixins 配置(NeoForge:neoforge.mods.toml `[[mixins]]`)、映射版本、重打 jar |
| 粒子**立即消失**/age 无效 | 寿命注入 Mixin 未生效 | 同上;`Particle.setLifetime` 对应注入点 | 同上 |
| 速度表达式/轨道**不执行** | tick 注入未生效 | `ParticleMixin` 注入点日志 | 同上 |
| 命令成功但**完全无粒子**(客户端在) | 客户端 payload 接收失败(5.2) | 客户端日志无 receiver 错误;双端版本一致;Fabric `ClientPlayNetworking` / NeoForge `payloadContentChannel` 注册点 | 两端重装同版本 jar;核对 payload ID 常量一致 |
| 客户端启动**崩溃**含 `Packet type already registered` | payload 重复注册(b904f4c 回归场景 C) | 4.3 流程 | 回退到 b904f4c 之后的代码 |
| 客户端连服立即**被踢**、服务端 `ClassCastException` | 服务端 payload 未注册(b904f4c 回归场景 B) | 4.2 流程;服务端日志异常堆栈 | 同上;禁止发布直到通过 |
| 双端版本不一致 | jar 分支/API 版本不匹配(5.3) | `fabric.mod.json` 的 `depends.minecraft=1.21.11`、两端 mod 版本 | 统一版本;Fabric API 同步 |
| 图片无粒子 | 图片路径/缓存(5.4) | 客户端日志 `IOException`/`invalid image path` | 图片放客户端 `particleImages/`;`clearcache` 后再试 |
| 视频无粒子 | javacv 缺失(5.5) | 客户端日志 `NoClassDefFoundError: org/bytedeco/*` | `javacv/` 放齐 4 个 jar(含 ffmpeg-platform),重启 |
| 粒子数量异常/凭空消失 | `maxParticleCount` 上限(2.12) | `config/particleex.json` | 调大上限或减少生成量 |
| 粒子生成明显卡顿 | conditional 采样点过多(2.6) | 估算:范围/step 三次方 | 减小范围、调大 step |
| 修改 config 后行为怪异 | JSON 损坏 | 启动日志 `ParticleExConfig` 抛 `IOException` → 启动失败 | 删除 `config/particleex.json` 重新生成 |

### 5.1 Mixin 未启用的表现细节

- 模组全部功能的粒子渲染依赖 5 个客户端 Mixin:fabric 侧 `particleex.mixins.json`
  `client` 列表(`IBillboardParticleMixin` / `IParticleMixin` / `ParticleManagerAccessor` /
  `ParticleManagerMixin` / `ParticleMixin`);NeoForge 侧同名逻辑注册于
  `neoforge.mods.toml`。`required=true`,注入失败会**阻止游戏启动**;
  - 若"注入了但没生效"(典型:命令全通、渲染全白/全瞬),检查点:
    1. 客户端是否真的有 jar(服务端装、客户端漏装是头号原因);
    2. 客户端日志搜索 `mixin` 关键字,分辨是"没有执行注入"还是"注入后未被调用";
    3. 多平台改造后检查 common 翻 Mojang 名时是否改动过注入目标方法签名
       (`ParticleManagerMixin` 目标类在 1.21.11 Yarn 为 `ParticleRenderer`,
        Mojang 官方名为 `ParticleEngine`/`ParticleGroup`,见
       `docs/multiloader-migration.md` 第 3 节映射表——**映射错位是改造期最常见的 Mixin 失效原因**)。

### 5.2 payload 收不到的表现细节

- 服务端执行命令返回成功、客户端无粒子、无任何日志报错——先分清三层:
  1. 注册层:客户端是否注册了对应的 globalReceiver(缺模组/改过 ID 常量);
  2. 编码层:payload 编解码对称性(`PayloadCodecUtil`,双端 jar 不一致时最典型);
  3. 送达层:服务端 `ServerPlayNetworking.send`(NeoForge `PacketDistributor`)对
     目标玩家是否生效(玩家不在 `PlayerLookup.world` 返回集合=加载维度问题)。
- 客户端日志开 `--dev` 或临时加 `DEBUG` 观察 fabric API 网络日志;
  无开发者环境时,用"局域网联机双开"复现:两个客户端互为验证端。

### 5.3 双端版本不一致的表现细节

- Fabric:`fabric.mod.json` 依赖 `minecraft "=1.21.11"`(精确等号),版本错直接拒绝加载,
  表现是客户端/服务端启动日志报 `fabricloader` 依赖解析失败——这是**好事**,早失败。
- 模组与 API 版本不一致(Fabric API 大变号):可能出现命令树可用但 payload 结构
  不兼容的隐性异常;连服时更常见 `Failed to decode packet` 类黄色警告后**无声无粒子**。
- NeoForge:两端 NeoForge 本体版本不一致时客户端根本连不上,不属于本模组问题。

### 5.4 图片粒子的位置细节(常见误配)

- `image` 命令的路径是 `particleImages/` **相对路径**(如 `logo.png`);
  允许子目录(`sub/logo.png`),但 canonical 校验拒绝 `..` 越界。
- 图片读取在**客户端**(`ImageUtil`),放服务端目录没用。
- 替换图片后旧图仍在的是缓存——先 `clearcache`。

### 5.5 视频的 javacv 位置细节

- `javacv/` 目录位于**客户端**运行目录;4 个 jar 缺一不可(javacv、javacpp、ffmpeg、ffmpeg-platform),
  且 **javacv/javacpp/ffmpeg 大版本需一致**(如全部 1.5.11);
- `ffmpeg-platform` 内置各平台 so/dll,缺失时表现为 `ClassNotFoundException` 而不是
  加载失败报错——不要被"命令成功"误导。

---

## 附:与测试相关的项目文件

- 命令实现:`common/src/main/java/com/noone/particleex/command/particlep/`
- 网络 payload:`common/src/main/java/com/noone/particleex/network/payload/`
- 客户端网络处理:fabric 侧 `fabric/src/main/java/com/noone/particleex/network/ClientNetworkHandler.java`(NeoForge 侧对应处理器改造中)
- 双端注册点:`common/src/main/java/com/noone/particleex/common/Bridge.java`(多平台抽象,见 `docs/multiloader-migration.md`)
- 回归提交:`b904f4c`(payload 注册修复)
- 命令案例库:`docs/particle-cases.md`(案例 1.4 等缺参命令以本文档核正版为准)