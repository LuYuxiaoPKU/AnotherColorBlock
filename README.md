# AnotherColorBlock

**社区维护版** · 粒子工具模组 · Minecraft **1.14.4 ~ 26.3**（Fabric + NeoForge 双加载器）

一个通过命令生成粒子的辅助模组：支持数学表达式、参数方程、图片、视频、粒子分组管理等功能。

**版本支持**：
- **Fabric**：1.14.4 ~ 26.3 全版本（29 个版本分支，各版本组对应独立 jar，见下方版本对照表）
- **NeoForge**：1.20.1 ~ 26.3 全版本（21 个版本分支）

---

## 下载与安装

### 1. 选择对应版本的 jar

按你的 Minecraft 版本，从 [Releases](../../releases) 或 [Actions 构建产物](../../actions) 获取对应分支的 jar：

| Minecraft 版本 | 使用分支 | Fabric | NeoForge |
|---|---|---|---|
| 26.3 | `26.3` | ✅ | ✅ |
| 26.2 | `26.2` | ✅ | ✅ |
| 26.1 | `26.1` | ✅ | ✅ |
| 1.21.11 | `1.21.11` | ✅ | ✅ |
| 1.21.10 | `1.21.10` | ✅ | ✅ |
| 1.21.9 | `1.21.9` | ✅ | ✅ |
| 1.21.8 | `1.21.8` | ✅ | ✅ |
| 1.21.7 | `1.21.7` | ✅ | ✅ |
| 1.21.6 | `1.21.6` | ✅ | ✅ |
| 1.21.5 | `1.21.5` | ✅ | ✅ |
| 1.21.4 | `1.21.4` | ✅ | ✅ |
| 1.21.3 | `1.21.3` | ✅ | ✅ |
| 1.21.2 | `1.21.2` | ✅ | ✅ |
| 1.21 | `1.21` | ✅ | ✅ |
| 1.21.1 | `1.21` 或 `1.21.1` | ✅ | ✅ |
| 1.20.6 | `1.20.6` | ✅ | ✅ |
| 1.20.5 | `1.20.5` | ✅ | ✅ |
| 1.20.4 | `1.20.4` | ✅ | ✅ |
| 1.20.3 | `1.20.3` | ✅ | ✅ |
| 1.20.2 | `1.20.2` | ✅ | ✅ |
| 1.20.1 | `1.20.1` | ✅ | ✅ |
| 1.20 | `1.20` | ✅ | ❌ |
| 1.19.4 / 1.19.3 | `1.19.4` | ✅ | ❌ |
| 1.19.2 / 1.19.1 / 1.19 | `1.19.2` | ✅ | ❌ |
| 1.18.2 / 1.18.1 / 1.18 | `1.18.2` | ✅ | ❌ |
| 1.17.1 / 1.17 | `1.17.1` | ✅ | ❌ |
| 1.16.5 | `1.16.5` | ✅ | ❌ |
| 1.15.2 | `1.15.2` | ✅ | ❌ |
| 1.14.4 | `1.14.4` | ✅ | ❌ |

上表为**逐版本实机测试通过的覆盖**（Fabric 37 站 + NeoForge 18 站，全部达标）。

**尚未覆盖的正式版**（列入后续计划，将逐步补测覆盖）：
- **1.14 ~ 1.14.3 / 1.15.1**：预计放宽现有分支 range 即可覆盖，待验证
- **1.16 ~ 1.16.4**：1.16.2 为 Registry 断代点，1.16.2~1.16.4 预计与 1.16.5 同 jar 系、1.16~1.16.1 接近 1.15.2 系，需独立验证
- **1.13.2 及更早**：暂无计划（Fabric 生态真空：官方工具链不含 1.13.2；1.12.2 无 Brigadier/数据包 function）

### 2. 安装要求

本模组为**服务端指令 + 客户端渲染**架构，需要两端同时安装才能正常工作：

- **多人服务器**：服务器本体和**所有玩家客户端**都必须安装本模组
    - 服务端负责注册 `/particleex` 指令并下发粒子数据
    - 客户端负责接收数据并渲染粒子（含粒子特效 Mixin）
- **单人游戏**：装一份即可（单机同时扮演服务端与客户端）
- **依赖**（两端都要）：
    - Fabric 线：Fabric Loader 0.17.0+ 与 Fabric API
    - NeoForge 线：对应版本的 NeoForge

### 3. Java 版本要求

| Minecraft 版本 | Java |
|---|---|
| 1.14.4 ~ 1.16.5 | 8+ |
| 1.17.x | 16+ |
| 1.18.x ~ 1.20.4 | 17+ |
| 1.20.5 ~ 1.21.x | 21 |
| 26.1 ~ 26.3 | 25 |

---

## 通用参数说明

| 参数        | 格式                  | 说明                                |
|-----------|---------------------|-----------------------------------|
| `<粒子>`    | `minecraft:heart` 等 | 任意粒子效果 ID                         |
| `<位置>`    | `x y z` / `~ ~ ~`   | 支持相对坐标（~ 表示当前玩家位置）                |
| `<颜色>`    | `红 绿 蓝 透明度`         | 4 个 0.0 ~ 1.0 的小数，如 `1 0 1 1`（粉红） |
| `<速度>`    | `vx vy vz`          | 粒子初始速度，`0 0 0` 表示静止               |
| `<范围>`    | `dx dy dz`          | 粒子分布范围（高斯分布标准差）                   |
| `<组>`     | `组名` 或 `null`       | 粒子分组，可用 `\|` 分隔加入多个组，便于统一管理       |
| `[age]`   | 整数                  | 粒子寿命（tick）：`-1` 永久，`0` 默认，`>0` 指定 |
| `[速度表达式]` | 字符串                 | 每 tick 执行的表达式（见"表达式"一节），`null` 关闭 |
| `[速度步长]`  | 小数                  | 速度表达式中 `t` 每 tick 的增量，默认 `1.0`    |

---

## 命令详解

### 1. normal — 普通粒子

```
/particleex normal <粒子> <位置> <颜色> <速度> <范围> <数量> [age] [速度表达式] [速度步长] [组]
```

在指定位置生成指定数量的粒子，位置带高斯随机偏移（范围即标准差）。

### 2. conditional — 条件粒子

```
/particleex conditional <粒子> <位置> <颜色> <速度> <范围> <表达式> [step] [age] [速度表达式] [速度步长] [组]
```

在范围内按 `step` 步长逐点采样，**表达式为真（非 0）的位置才生成粒子**。`step` 默认 `0.1`。

表达式为 `null` 时无条件生成范围内所有采样点。

### 3. parameter 系列 — 参数方程粒子

根据参数方程生成粒子图案，`t` 从 `<起始>` 遍历到 `<结束>`，步长 `[step]`（默认 `0.1`）。

**基本语法**（普通参数，xyz 直角坐标）：
```
/particleex parameter <粒子> <位置> <颜色> <速度> <起始> <结束> <表达式> [step] [age] [速度表达式] [速度步长] [组]
```

**变体**（共 8 种，把 `parameter` 换成下列前缀即可）：

| 命令                       | 表达式变量                | 附加参数    | 说明                                          |
|--------------------------|----------------------|---------|---------------------------------------------|
| `parameter`              | `x,y,z`              | —       | 直角坐标参数方程                                    |
| `polarparameter`         | `s1,s2,dis`          | —       | 球坐标：`x=dis·cos(s2)·cos(s1)`，`y=dis·sin(s2)` |
| `tickparameter`          | `x,y,z`              | `<cpt>` | 分 tick 生成（每 tick 生成 cpt 个，避免一次性卡顿）          |
| `tickpolarparameter`     | `s1,s2,dis`          | `<cpt>` | 球坐标 + 分 tick                                |
| `rgbaparameter`          | `x,y,z,cr,cg,cb`     | —       | 可同时控制颜色（**无需颜色参数**）                         |
| `rgbapolarparameter`     | `s1,s2,dis,cr,cg,cb` | —       | 球坐标 + 颜色                                    |
| `rgbatickparameter`      | `x,y,z,cr,cg,cb`     | `<cpt>` | 直角坐标 + 颜色 + 分 tick                          |
| `rgbatickpolarparameter` | `s1,s2,dis,cr,cg,cb` | `<cpt>` | 全部组合                                        |

tick 系列的语法（`<cpt>` 在 `[step]` 之后）：
```
/particleex tickparameter <粒子> <位置> <颜色> <速度> <起始> <结束> <表达式> [step] <cpt> [age] [速度表达式] [速度步长] [组]
```

**示例**：竖立的爱心（经典心形线，宽 1.6 高 1.8，停留 10 秒）
```
/particleex parameter minecraft:heart ~ ~1.5 ~ 1 0 1 1 0 0 0 0 6.28318 "x,y,z=1.6*sin(t)^3,1.8*cos(t)-0.6*cos(2*t)-0.2*cos(3*t)-0.1*cos(4*t),0" 0.02 200
```

**示例**：极坐标螺旋（`polarparameter`）
```
/particleex polarparameter minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 0 100 "s1,s2,dis=t/5,t/50,1" 0.1 200
```

### 4. image / imagematrix — 图片粒子

```
/particleex image <粒子> <位置> <路径> [缩放] [x旋转] [y旋转] [z旋转] [翻转] [dpb] [速度] [age] [速度表达式] [速度步长] [组]
/particleex imagematrix <粒子> <位置> <路径> [缩放] <矩阵> [dpb] [速度] [age] [速度表达式] [速度步长] [组]
```

- `<路径>`：图片在游戏目录 **`particleImages/`** 下的相对路径（png/jpg 等）
- `[缩放]`：默认 `0.1`（每个像素约 0.1 格）
- `[旋转]`：90 的倍数（90/180/270）
- `[翻转]`：`not` / `horizontally` / `vertically`
- `[dpb]`：密度（每方块粒子数），默认 `10`，越小越稀疏
- `imagematrix` 额外支持 4x4 变换矩阵：`E3`、`E4` 或 `"(1,0,0,0,,0,1,0,0,,0,0,1,-100,,0,0,0,1)"`（行列间用 `,,` 分隔）

**示例**：在面前显示图片 logo.png，放大 0.5 倍
```
/particleex image minecraft:end_rod ~ ~2 ~ logo.png 0.5 0 0 0 not 10
```

### 5. video / videomatrix — 视频粒子

```
/particleex video <粒子> <位置> <路径> [缩放] [x旋转] [y旋转] [z旋转] [翻转] [dpb] [速度] [age] [速度表达式] [速度步长] [组]
/particleex videomatrix <粒子> <位置> <路径> [缩放] <矩阵> [dpb] [速度] [age] [速度表达式] [速度步长] [组]
```

- `<路径>`：视频在游戏目录 **`particleVideos/`** 下的相对路径
- 其余参数与 image 一致
- **需要 javacv**：见下文"视频功能说明"

### 6. group — 粒子分组管理

```
/particleex group remove <组> [表达式] [位置]
/particleex group change <parameter|speedexpression> <组> <表达式> [条件表达式] [位置]
```

- `group remove`：移除组内粒子；`表达式` 为条件（满足才移除，如 `"age>100"`），省略则全部移除；`位置` 用于计算相对坐标（默认玩家位置）
- `group change`：
  - `parameter`：修改组内粒子的属性（位置/颜色/速度等）
  - `speedexpression`：替换组内粒子的运动表达式
  - `条件表达式`：只对满足条件的粒子生效

**示例**：移除 group1 中存活超过 100 tick 的粒子
```
/particleex group remove group1 "age>100"
```

### 7. 其他

```
/particleex clearparticle   清除所有模组生成的粒子
/particleex clearcache      清空图片缓存（替换图片后生效）
/particleex functionlist    查看表达式可用函数列表
```

---

## 表达式语法

**赋值**：`变量1,变量2=表达式1,表达式2`（多变量一次赋值），多条语句用 `;` 分隔。
**运算**：`+ - * / % ^`（`^` 为幂）、比较 `> < >= <= == !=`、逻辑 `& | !`。
**常量**：`PI`、`E`。
**矩阵**：`(1,2,3,4,,5,6,7,8)`，行列之间用 `,,` 分隔。

**可用变量**：

| 变量                          | 含义                                  |
|-----------------------------|-------------------------------------|
| `x,y,z`                     | 粒子相对中心偏移                            |
| `s1,s2,dis`                 | 球坐标（水平角、仰角、距离）                      |
| `t`                         | 参数（parameter 的遍历值 / 速度表达式的 tick 计数） |
| `vx,vy,vz`                  | 速度                                  |
| `cr,cg,cb,alpha`            | 颜色与透明度（0~1）                         |
| `cx,cy,cz`                  | 图案中心坐标                              |
| `dx,dy,dz` / `ds1,ds2,ddis` | 粒子初始偏移及其球坐标                         |
| `age`                       | 粒子已存活 tick                          |
| `destroy`                   | 设为非 0 立即销毁粒子                        |

**速度表达式**（`[速度表达式]` 参数）每 tick 执行一次，常用写法：
- 控制移动：`"vx,vz=-z*0.1,x*0.1"`（绕中心旋转）
- 控制颜色：`"cr,cg,cb=t/100%1,0,0"`（随时间变红）
- 销毁：`"destroy=age>200"`

**函数**：`sin cos tan asin acos atan toRadians toDegrees exp log log10 sqrt cbrt ceil floor rint atan2 pow round random addExact subtractExact multiplyExact incrementExact decrementExact negateExact floorDiv floorMod abs max min ulp signum sinh cosh tanh hypot expm1 log1p copySign getExponent nextAfter nextUp nextDown scalb` 等（完整列表见 `/particleex functionlist`）

**更多示例**（正弦波曲线）：
```
/particleex parameter minecraft:end_rod ~ ~5 ~ 1 1 1 1 0 0 0 0 20 "x,y,z=t,sin(t),0" 0.05 300
```

---

## 视频功能说明

模组本身**不打包**任何视频解码库。如需使用 `/particleex video`，请自行下载 javacv 相关 jar 并放入游戏目录下的 `javacv/` 文件夹：

```
javacv-1.5.10.jar
javacpp-1.5.10.jar
ffmpeg-1.5.10-1.5.10.jar
ffmpeg-platform-1.5.10-1.5.10.jar
```

（可从 Maven Central 获取，使用前请确认其许可条款符合你的用途。）

---

## 粒子案例库

[docs/particle-cases.md](docs/particle-cases.md) 收录了一批改写为本模组语法的社区案例（螺旋、渐变、球壳、甜甜圈等），供快速上手参考。表达式引擎的更多函数玩法与复杂实例仍在持续探索中，后续案例将陆续补充。

---

## 构建

```bash
# 需要 JDK 21+（26.x 需 JDK 25）与网络环境
./gradlew build          # 构建全部模块
./gradlew :fabric:build  # 仅 Fabric 模块
./gradlew :neoforge:build  # 仅 NeoForge 模块
```

仓库为三模块结构：

```
common/    平台无关核心（表达式引擎、命令定义、数据包载荷）
fabric/    Fabric 加载器适配（1.14.4 ~ 26.3）
neoforge/  NeoForge 加载器适配（1.20.1 ~ 26.3）
```

各版本分支从主开发线派生，仅保留版本适配差异（见 `docs/version-branches.md` 的分支差异手册）。

---

## 鸣谢

本项目站在以下作者与团队的肩膀上，谨致谢意：

### ColorBlock 原模组线（本项目直接前身）

| 作者/团队 | 贡献 | 主页 |
|---|---|---|
| **Is_GK** | ColorBlock 原模组作者，表达式粒子指令的开创者 | [GitHub](https://github.com/IslenautsGK) · [Bilibili](https://space.bilibili.com/12657339) · [MC百科](https://www.mcmod.cn/author/23085.html) |
| **Junior_Mo** | ColorBlock 原模组作者（MCBBS 原帖发布者） | [Bilibili](https://space.bilibili.com/10836361) · [MC百科](https://www.mcmod.cn/author/25217.html) |
| **Another Era**（团队） | ColorBlock 相关开发团队 | [MC百科](https://www.mcmod.cn/author/22704.html) |

原模组代码仓库：[IslenautsGK/ColorBlock](https://github.com/IslenautsGK/ColorBlock)

### 社区维护线

| 作者/团队 | 贡献 | 主页 |
|---|---|---|
| **noone89** | 社区维护版创始人（AnotherColorBlock，1.21~26.2 移植） | [GitHub](https://github.com/noone89A) · [Bilibili](https://space.bilibili.com/496649264) |

### ExParticle 线（表达式引擎语法同源的高版本延续项目）

| 作者/团队 | 贡献 | 主页 |
|---|---|---|
| **hackermdch** | ExParticle 作者 | [GitHub](https://github.com/hackermdch) · [Bilibili](https://space.bilibili.com/243670887) · [MC百科](https://www.mcmod.cn/author/30552.html) |
| **翊尘呀** | ExParticle 贡献者（透明度修复等） | [GitHub](https://github.com/YiWolf233) · [Bilibili](https://space.bilibili.com/615927675) |

### 第三方组件

- [JavaCV / JavaCPP / FFmpeg](https://github.com/bytedeco/javacv)：视频粒子功能的解码基座（不随本模组分发）

### 案例参考

粒子案例库中的部分案例整理自社区同类模组（EffectLib / ParticleAnimationLib / Particle Storm / MoreParticle 等）并改写为本模组语法，出处见 [docs/particle-cases.md](docs/particle-cases.md)。

---

## 版权声明与免责条款（重要，请阅读）

### 1. 项目来源

本项目是模组 **AnotherColorBlock** 的社区维护版本。

- 原模组作者以 **CC0 1.0 Universal**（公共领域贡献）协议发布原作品。
- 原模组已停止维护。本项目在原作品基础上进行重写、修复与适配，**名称沿用 AnotherColorBlock**（开发代号 ParticleEx，Mod ID 为 `particleex`）。
- 原作品发布在 **MCBBS（Minecraft 中文论坛）**，该论坛现已关闭；原发布帖可查阅 MCBBS 存档站：<https://archives.mcbbs.co/read.php?tid=917845>（[1.16.5-1.12.2][ColorBlock——新的粒子特效拓展]）。
- CC0 协议全文：<https://creativecommons.org/publicdomain/zero/1.0/legalcode>

### 2. 授权说明

- 原模组中来源于 CC0 作者的部分：按 **CC0 1.0 Universal** 许可使用，允许任何人自由使用、修改与再分发，包括商用。
- 本项目新增与修改的代码：按 **GNU General Public License v3.0（GPL-3.0）** 许可（Copyright (c) 2026 noone89）。使用、复制、修改与再分发须遵循 GPL-3.0 条款，包括但不限于：保留版权声明；衍生作品必须以 GPL-3.0 发布并公开源码。
- 本项目由作者**亲自维护**，不依赖社区贡献；如有功能需求或问题，请以 Issue 形式反馈。
- CC0 原作品并入本项目合法（CC0 允许以任何协议再发布），本项目代码不得再以更宽松协议（如 MIT/BSD）对外许可。

### 2.1 关于 NeoForge 版本

- 本项目提供 **NeoForge 版本**（与 Fabric 版本同仓构建，三模块结构 common/fabric/neoforge），覆盖 **1.20.1 ~ 26.3** 全版本，与 Fabric 版本同标准实机测试（18 站全部达标）。
- Fabric 与 NeoForge 两端均需与服务器版本对应分支的 jar 配对使用。

### 2.2 AI 辅助开发声明

本项目在社区维护期间使用 AI 编程辅助工具进行开发（Claude Code 框架，接入 DeepSeek V4.1-Flash、DeepSeek V4-Flash、GLM-5.3-Flash、Qwen3.8-27B 四款模型，排名不分先后），参与版本移植、适配修复与测试自动化。全部代码经多版本实机测试（Fabric 37 站 + NeoForge 18 站，覆盖 1.14.4~26.3 每个正式版）。

AI 使用量统计（token 消耗）公开可查：[docs/token-usage.md](docs/token-usage.md)。

### 3. 免责声明（免责条款）

本模组按 **"原样"（AS-IS）** 提供，**不附带任何形式的明示或暗示保证**，包括但不限于：

- 适销性保证；
- 特定用途适用性保证；
- 不侵权保证；
- 功能完整性、稳定性、安全性保证。

作者**不对**以下事项承担任何责任：

- 使用本模组造成的存档损坏、世界数据丢失；
- 使用本模组造成的客户端或服务端崩溃、性能问题；
- 使用本模组违反服务器规则或平台条款所产生的后果；
- 因第三方依赖（包括但不限于 javacv/FFmpeg）引发的任何问题；
- 任何直接、间接、偶然、特殊或后果性损害。

### 4. 关联声明

- 本项目与原作者**无任何隶属、赞助或认可关系**；
- 本项目与 **Mojang Studios / Microsoft** 无任何关联，Minecraft 及所有相关名称、商标归 Mojang Studios / Microsoft 所有；
- 本项目不是原模组的官方续作，任何使用本模组造成的误解与原作者无关。

### 5. 第三方组件

| 组件                        | 用途   | 许可                                                        |
|---------------------------|------|-----------------------------------------------------------|
| javacv / javacpp / FFmpeg | 视频解码 | GPL v2（含 Classpath Exception），**不随本模组分发**，需自行获取，使用时请遵守其许可 |

---

## 协议

- 原作品：CC0 1.0 Universal
- 本项目代码：GNU General Public License v3.0（GPL-3.0）
- 许可全文见 `LICENSE` 文件
