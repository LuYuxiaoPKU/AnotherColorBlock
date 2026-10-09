# AnotherColorBlock 粒子案例库（吸收自社区同类模组）

> 本文档收集 Minecraft 粒子特效生态中与 AnotherColorBlock 功能相似的模组案例，
> 全部改写为 AnotherColorBlock 命令语法，可直接复制使用。案例出处与许可注意见文末。

## 1. 参数方程图案类

### 1.1 上升螺旋（源自 EffectLib 官方参数方程示例）

[EffectLib（Bukkit 插件）](https://github.com/elBukkit/EffectLib/wiki/Parametric-Equations) 用 `xEquation: "sin(0.3141*t)" / yEquation: "0.5t" / zEquation: "cos(0.3141*t)"` 生成上升旋涡。
AnotherColorBlock 等价的 parameter 命令：

```
/particleex parameter minecraft:end_rod ~ ~ ~ 1 1 1 1 0 0 0 0 300 "x,y,z=sin(0.3141*t),0.5*t,cos(0.3141*t)" 0.1 300
```

要点：`t` 每 tick 自增，`0.3141 ≈ π/10` 使螺旋每 20 tick 一圈，配合 `0.5*t` 匀速上升。

### 1.2 圆周轨道（源自 Particle Storm loading 示例）

[Particle Storm](https://github.com/westernat/ParticleStorm) 的环形轨道用 `variable.emitter_age*360` 做角度、`variable.radius` 做半径。
AnotherColorBlock 用速度表达式让已生成粒子绕中心旋转（README 经典写法）：

```
/particleex normal minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 20 "vx,vz=-z*0.1,x*0.1" 1 60
```

### 1.3 渐变渐变粒子（源自 Particle Storm blend 示例的三段透明度）

[Particle Storm blend.json](https://github.com/westernat/ParticleStorm/tree/neoforge/1.21.1/src/main/resources/assets/particlestorm/particle_definitions) 用 `interpolant: "v.particle_age / v.particle_lifetime"` + 渐变梯度实现出生→峰值→淡出。
AnotherColorBlock 用速度表达式控制颜色随时间变化：

```
/particleex normal minecraft:dust ~ ~2 ~ 1 0 1 1 0 0 0 40 200 "cr,cg,cb=1,1-t/200,1-t/200" 1
```

（粉色粒子 200 tick 内蓝绿色通道线性衰减，模拟淡出效果。）

### 1.4 球壳与甜甜圈（条件表达式采样）

AnotherColorBlock 的 `conditional` 命令对范围内逐点采样、表达式为真才生成。利用该特性可生成解析几何图形：

球壳（半径 1，壳厚 0.1）：
```
/particleex conditional minecraft:end_rod ~ ~3 ~ 1 1 1 1 0 0 0 "abs(x*x+y*y+z*z-1)<0.05" 0.08 200
```

甜甜圈（环面，主半径 1.5、管半径 0.4）：
```
/particleex conditional minecraft:end_rod ~ ~3 ~ 1 1 1 1 0 0 0 "abs(sqrt(x*x+z*z)-1.5)^2+y*y<0.16" 0.06 200
```

## 2. 图片与文字类

### 2.1 图片粒子（源自 ParticleAnimationLibCommands 的 image 效果）

[PAL Commands](https://emafire003.gitbook.io/particleanimationlibwiki/commands) 的 `/pal image` 用 dust 粒子逐像素拼图，支持远程 URL、本地路径与数据包纹理 identifier，并支持跳像素采样（stepX/stepY）省性能。
AnotherColorBlock 的 image 命令（图片放游戏目录 `particleImages/`）：

```
/particleex image minecraft:dust ~ ~2 ~ logo.png 0.1 0 0 0 not 8
```

`[dpb]`（每方块粒子数）对应 PAL 的 step 参数，调小即稀疏采样、省粒子数。

### 2.2 文字粒子（源自 PAL 的 text 效果思路）

PAL 用粒子逐像素渲染文字（支持系统字体）。AnotherColorBlock 可先用任意工具把文字渲染成透明底 PNG 存入 `particleImages/`，再走 2.1 的 image 命令：

```
/particleex image minecraft:end_rod ~ ~2 ~ "text_hello.png" 0.05 0 0 0 not 10 100
```

（建议白字黑底→用颜色参数统一染色。）

## 3. 图案模板（源自 PAL 的几何效果，改写为参数方程）

PAL 提供球体/甜甜圈/涡旋/圆环/线段/圆锥等内置图案。AnotherColorBlock 用 parameter 表达式实现等价效果：

**球面螺旋**（极坐标，s1=水平角、s2=仰角、dis=半径）：
```
/particleex polarparameter minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 0 314 "s1,s2,dis=t/10,sin(t/10)*0.5,2" 0.1 200
```

**涡旋**（半径渐增的螺旋）：
```
/particleex parameter minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 0 200 "x,y,z=(t/100)*sin(t/5),(t/100)*cos(t/5)*0.3,t/100*cos(t/5)" 0.1 300
```

**线段**（两点间插值，PAL line 的等价形式）：
```
/particleex parameter minecraft:flame ~ ~ ~ 1 1 1 1 0 0 0 0 1 "x,y,z=t*3,0,0" 0.05 100
```

## 4. 分组管理（源自 MoreParticle 的 tag 机制）

[MoreParticle](https://modrinth.com/mod/more-particle) 用粒子命令尾部的 `tag:<标签>` + `killparticle <玩家> <tag>` 批量管理粒子。
AnotherColorBlock 的 group 命令功能更强（支持条件移除与属性修改），对应写法：

```
/particleex normal minecraft:end_rod ~ ~2 ~ 1 1 1 1 0 0 0 50 null null 1 heart
/particleex group remove heart "age>100"
/particleex group change speedexpression heart "vx,vz=-z*0.1,x*0.1"
```

## 5. 图片→粒子命令（源自 ParticleConverter 思路）

[ParticleConverter](https://github.com/kemo14331/Particle-Converter)（工具，非 mod）把图片离线转换为原版 /particle 命令串，[bilibili 教程 BV1LN4y1K7x7](https://www.bilibili.com/video/BV1LN4y1K7x7)。AnotherColorBlock 已内置图片粒子，无需离线转换；该工具可作无 mod 环境的替代方案参考。

---

## 对标结论（调研记录）

2026-10-09 全网调研：**不存在功能完全对齐 AnotherColorBlock 的 mod**。最接近者：

| mod | 平台/版本 | 与 AnotherColorBlock 的重合点 | 参考价值 |
|---|---|---|---|
| [MoreParticle](https://modrinth.com/mod/more-particle) | Fabric 1.21.x | 命令+表达式(calc)+纹理(seqt)+分组(tag) | 命令字段设计 |
| [ParticleAnimationLibCommands](https://modrinth.com/mod/particleanimationlibcommands) | Fabric 1.20–1.21.5 | 命令+图片+文字+几何图案 | 案例 2/3 |
| [Particle Storm](https://www.mcmod.cn/class/16865.html) | Forge/NeoForge 1.20.1/1.21.1 | Molang 表达式+资源包 JSON | 渐变/轨道表达式 |
| [EffectLib](https://github.com/elBukkit/EffectLib/wiki/Parametric-Equations) | Bukkit 插件 | 参数方程（t/t2 曲面） | 案例 1 |
| [Particle Shapes](https://modrinth.com/mod/particle-shapes) | Fabric 1.21.x | 内置几何图案命令 | — |

**独有卖点**：全生态无同类支持**视频粒子**（AnotherColorBlock 的 video/videomatrix 为独有功能）。

## 来源与许可注意

- 本文档案例为**命令/表达式用法示例**，按 AnotherColorBlock 语法改写（AnotherColorBlock 自带表达式语法与各 mod 语法不同，未逐字复制原文）；改写内容按项目 LICENSE（GPL-3.0）发布。
- 参考来源（均为公开文档/仓库页面，2026-10-09 访问）：
  - EffectLib Wiki（[Parametric-Equations](https://github.com/elBukkit/EffectLib/wiki/Parametric-Equations)，MIT，elBukkit）
  - Particle Storm（[粒子定义示例](https://github.com/westernat/ParticleStorm/tree/neoforge/1.21.1/src/main/resources/assets/particlestorm/particle_definitions)，MIT，westernat）
  - ParticleAnimationLibCommands（[官方 Wiki](https://emafire003.gitbook.io/particleanimationlibwiki/commands)，MIT，Emafire003）
  - MoreParticle（[README](https://github.com/BottleSoy/more-particle)，BottleSoy）
  - ParticleConverter（[GitHub](https://github.com/kemo14331/Particle-Converter)）
- 若需在文档中引用上述 mod 的原始命令示例，请各自查阅其开源许可；涉及 MOD 名称均为各作者商标，此处仅为对照引用。
