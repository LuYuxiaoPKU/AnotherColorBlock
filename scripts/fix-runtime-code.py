#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""批量修复 19 个分支的"服务器端初始化 client 类"运行时 bug（代码部分）。

发布级 bug：三个入口文件在服务器端裸调用 MessageBridge.setSink(new ClientMessageUtil())，
ClientMessageUtil 引用 net.minecraft.client.*，服务器端 NoClassDefFoundError 崩溃。
修复 = 把 setSink 移入仅客户端入口（fabric: ParticleExClient；neoforge: dist 判断包裹）。

分线写法（FML API 实证）：
- 26.x（FML 11.0）：FMLEnvironment.getDist()   —— dist 静态字段已删除
- 1.21 / 1.20.2-6（FML ≤10.0 / NeoForge 20.x）：FMLEnvironment.dist 静态字段
- 1.20.1（Forge 47）：net.minecraftforge.fml.loading.FMLEnvironment.dist（forge 包名）

用法：
  python fix-runtime-code.py --dry-run        # 只输出每分支将做的改动，不落盘
  python fix-runtime-code.py 26.1 26.3        # 指定分支执行（需在该分支 checkout 状态）
"""
import sys
import os
import subprocess
import re

ROOT = os.getcwd()  # 在仓库根目录执行（脚本可能被复制到 /tmp 运行，不能用 __file__ 推导）

# ---- 分线参数表 ----
GET_DIST_BRANCHES = {"26.1", "26.2", "26.3"}          # FML 11.0 → getDist()
FORGE_PKG_BRANCHES = {"1.20.1"}                        # Forge 47 → net.minecraftforge

FABRIC_SERVER = "fabric/src/main/java/com/noone/particleex/ParticleEx.java"
FABRIC_CLIENT = "fabric/src/main/java/com/noone/particleex/ParticleExClient.java"
NEOFORGE_ENTRY = "neoforge/src/main/java/com/noone/particleex/NeoForgeEntry.java"

IMPORT_CMU = "import com.noone.particleex.util.ClientMessageUtil;"
IMPORT_MB = "import com.noone.particleex.util.MessageBridge;"
SETSINK = "MessageBridge.setSink(new ClientMessageUtil());"


def warn(branch, msg):
    print(f"  [!] {branch}: {msg}")


def fix_fabric_server(branch, lines, dry):
    """ParticleEx.java（ModInitializer，双端都执行）：删除 setSink 与两个 util import。"""
    changed = []
    src = "\n".join(lines)
    if SETSINK not in src:
        warn(branch, "ParticleEx.java 无裸 setSink（已修复或结构不同）")
        return lines, changed
    out = [ln for ln in lines
           if SETSINK not in ln and IMPORT_CMU != ln and IMPORT_MB != ln]
    if len(out) != len(lines):
        changed.append("ParticleEx.java: 删 setSink + 2 util import")
    return out, changed


def fix_fabric_client(branch, lines, dry):
    """ParticleExClient.java（仅客户端）：onInitializeClient 首行加 setSink + 补 2 import。"""
    changed = []
    src = "\n".join(lines)
    if SETSINK in src:
        return lines, changed  # 已有
    if "onInitializeClient" not in src:
        warn(branch, "ParticleExClient.java 未找到 onInitializeClient，跳过")
        return lines, changed
    out = []
    inserted_imports = False
    inserted_body = False
    seen_method = False
    for ln in lines:
        # import 插在第一个 com.noone.particleex.network import 之前（保持包内分组）
        if not inserted_imports and ln.startswith("import com.noone.particleex.network"):
            if IMPORT_CMU not in src:
                out.append(IMPORT_CMU)
            if IMPORT_MB not in src:
                out.append(IMPORT_MB)
            inserted_imports = True
        out.append(ln)
        # 定位 onInitializeClient 方法体：签名行内 { → 该行后插；签名行无 { → 等独立 { 行
        if not seen_method and not inserted_body and "onInitializeClient" in ln:
            code_part = ln.split("//")[0]
            seen_method = True
            if "{" in code_part:
                out.append("      " + SETSINK)
                inserted_body = True
        if not inserted_body and seen_method and ln.strip() == "{":
            out.append("      " + SETSINK)
            inserted_body = True
    # 兜底：方法体没有独立 { 行（单行风格）时在方法名后插入
    if not inserted_body:
        warn(branch, "ParticleExClient.java 方法体 { 未独立成行，请人工检查")
    if inserted_body or inserted_imports:
        changed.append(f"ParticleExClient.java: 加 setSink{' + 2 import' if inserted_imports else ''}")
    return out, changed


def fix_neoforge_entry(branch, lines, dry):
    """NeoForgeEntry.java：setSink 包进 dist.isClient() 判断 + 补 FMLEnvironment import。"""
    changed = []
    src = "\n".join(lines)
    if "FMLEnvironment" in src:
        if SETSINK in src and "isClient()" in src:
            return lines, changed  # 已修复
        warn(branch, "NeoForgeEntry.java 已含 FMLEnvironment 但形态未知，跳过")
        return lines, changed
    if SETSINK not in src:
        warn(branch, "NeoForgeEntry.java 无裸 setSink（结构不同），跳过")
        return lines, changed
    if branch in GET_DIST_BRANCHES:
        fml_import = "import net.neoforged.fml.loading.FMLEnvironment;"
        guard_open = "        if (FMLEnvironment.getDist().isClient()) {"
    else:
        fml_import = ("import net.minecraftforge.fml.loading.FMLEnvironment;"
                      if branch in FORGE_PKG_BRANCHES
                      else "import net.neoforged.fml.loading.FMLEnvironment;")
        guard_open = "        if (FMLEnvironment.dist.isClient()) {"
    out = []
    inserted_import = False
    for ln in lines:
        # import 插在第一个 net.neoforged / net.minecraftforge import 之前
        if not inserted_import and ln.startswith(("import net.neoforged", "import net.minecraftforge")):
            out.append(fml_import)
            inserted_import = True
        if SETSINK in ln:
            out.append(guard_open)
            out.append("            " + SETSINK)
            out.append("        }")
            changed.append(f"NeoForgeEntry.java: setSink 包进 isClient 判断 + {fml_import}")
        else:
            out.append(ln)
    return out, changed


def process(branch, dry):
    print(f"=== {branch}")
    # 确认当前 checkout
    cur = subprocess.run(["git", "rev-parse", "--abbrev-ref", "HEAD"],
                         cwd=ROOT, capture_output=True, text=True).stdout.strip()
    if cur != branch:
        warn(branch, f"当前 checkout 是 {cur}，请先 git checkout {branch}")
        return
    for path, fn in ((FABRIC_SERVER, fix_fabric_server),
                     (FABRIC_CLIENT, fix_fabric_client),
                     (NEOFORGE_ENTRY, fix_neoforge_entry)):
        full = os.path.join(ROOT, path)
        if not os.path.exists(full):
            warn(branch, f"缺文件 {path}")
            continue
        with open(full, encoding="utf-8") as f:
            lines = f.read().split("\n")
        new_lines, changed = fn(branch, lines, dry)
        if changed:
            print("   " + " / ".join(changed))
            if not dry:
                with open(full, "w", encoding="utf-8", newline="\n") as f:
                    f.write("\n".join(new_lines))


if __name__ == "__main__":
    args = sys.argv[1:]
    dry = "--dry-run" in args
    branches = [a for a in args if not a.startswith("--")]
    if dry:
        print("# DRY-RUN：不落盘，仅报告")
    for b in branches:
        process(b, dry)
    if dry:
        print("# dry-run 完成。执行：git checkout <branch> && python fix-runtime-code.py <branch>")
