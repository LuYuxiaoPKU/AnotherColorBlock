# AnotherColorBlock（ParticleEx）

**社区维护版** · 粒子工具模组 · Minecraft 1.21.8 (Fabric)

一个通过指令生成粒子的辅助模组：支持数学表达式、参数方程、图片、视频、粒子分组管理等功能。

所有指令以 `/particleex` 为根命令，需要**权限等级 2**。

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

## 版权声明与免责条款（重要，请阅读）

### 1. 项目来源

本项目是模组 **AnotherColorBlock** 的社区维护版本。

- 原模组作者以 **CC0 1.0 Universal**（公共领域贡献）协议发布原作品。
- 原模组已停止维护。本项目在原作品基础上进行重写、修复与适配，并更名为 **ParticleEx**。
- 原作品发布在 **MCBBS（Minecraft 中文论坛）**，该论坛现已关闭，原发布页已不可访问；如需查阅原模组历史信息，可尝试通过互联网档案馆（Wayback Machine）检索快照。
- CC0 协议全文：<https://creativecommons.org/publicdomain/zero/1.0/legalcode>

### 2. 授权说明

- 原模组中来源于 CC0 作者的部分：按 **CC0 1.0 Universal** 许可使用，允许任何人自由使用、修改与再分发，包括商用。
- 本项目新增与修改的代码：按 **GNU General Public License v3.0（GPL-3.0）** 许可（Copyright (c) 2024-2026 noone89）。使用、复制、修改与再分发须遵循 GPL-3.0 条款，包括但不限于：保留版权声明；衍生作品必须以 GPL-3.0 发布并公开源码。
- 本项目由作者**亲自维护**，不依赖社区贡献；如有功能需求或问题，请以 Issue 形式反馈。
- CC0 原作品并入本项目合法（CC0 允许以任何协议再发布），本项目代码不得再以更宽松协议（如 MIT/BSD）对外许可。

### 2.1 关于 NeoForge 版本

- NeoForge 版本已由社区维护，存在社区维护版本，需要者请自行寻找。
- 本项目作者**仅维护 Fabric 版本**，暂时**不提供** NeoForge 版本的移植与维护。
- 请勿以本项目名义要求 NeoForge 支持；NeoForge 相关咨询请前往对应的社区维护版。

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

## 构建

```bash
# 需要 JDK 21 与网络环境
./gradlew build
```

产物位于 `build/libs/AnotherColorBlock-*.jar`。

## 协议

- 原作品：CC0 1.0 Universal
- 本项目代码：GNU General Public License v3.0（GPL-3.0）
- 许可全文见 `LICENSE` 文件
