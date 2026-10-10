#!/bin/bash
# 批量修复 19 个版本分支的两个发布级运行时 bug：
# 1. fabric.mod.json: depends "fabric" → "fabric-api"（26.x/1.21/1.20 全线，官方模板实证）
# 2. neoforge.mods.toml: minecraft versionRange 写死（1.21.11/26.3 复制残留）→ 对齐 gradle.properties 的 minecraft_version_range
# 用法：在仓库根目录执行；每个分支 checkout → 改 → commit → push
set -euo pipefail

BRANCHES="1.20.1 1.20.2 1.20.4 1.20.6 1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11 26.1 26.2 26.3"

for b in $BRANCHES; do
  echo "=== $b"
  git checkout "$b" >/dev/null 2>&1
  RANGE=$(grep '^minecraft_version_range=' gradle.properties | cut -d= -f2)
  echo "  range=$RANGE"

  # 1) fabric.mod.json：fabric → fabric-api（用 python 精确改 JSON，避免 sed 转义）
  FMJ="fabric/src/main/resources/fabric.mod.json"
  if grep -q '"fabric": "\*"' "$FMJ"; then
    python - "$FMJ" <<'PY'
import json, sys
p = sys.argv[1]
with open(p, encoding='utf-8') as f:
    d = json.load(f)
dep = d.get('depends', {})
if 'fabric' in dep and 'fabric-api' not in dep:
    dep['fabric-api'] = dep.pop('fabric')
    with open(p, 'w', encoding='utf-8', newline='\n') as f:
        json.dump(d, f, indent=2, ensure_ascii=False)
        f.write('\n')
    print('  fabric.mod.json: fabric -> fabric-api')
else:
    print('  fabric.mod.json: 无需修改（或已含 fabric-api）')
PY
  else
    echo "  fabric.mod.json: 未发现 fabric 依赖"
  fi

  # 2) neoforge.mods.toml：versionRange 对齐 RANGE（替换复制残留的 1.21.11/26.3 范围）
  TOML="neoforge/src/main/resources/META-INF/neoforge.mods.toml"
  if [ -f "$TOML" ]; then
    CUR=$(grep 'versionRange = "\[1.21.11,1.22)"\|versionRange = "\[26.3,26.4)"' "$TOML" | head -1 || true)
    if [ -n "$CUR" ]; then
      python - "$TOML" "$RANGE" <<'PY'
import sys
p, new = sys.argv[1], sys.argv[2]
with open(p, encoding='utf-8') as f:
    lines = f.readlines()
changed = 0
for i, ln in enumerate(lines):
    if 'versionRange = "[1.21.11,1.22)"' in ln or 'versionRange = "[26.3,26.4)"' in ln:
        lines[i] = f'versionRange = "{new}"\n'
        changed += 1
with open(p, 'w', encoding='utf-8', newline='\n') as f:
    f.writelines(lines)
print(f'  neoforge.mods.toml: 修正 {changed} 处 -> {new}')
PY
    else
      echo "  neoforge.mods.toml: 未发现需修正的版本范围"
    fi
  else
    echo "  neoforge.mods.toml: 文件不存在（跳过）"
  fi

  if git diff --quiet; then
    echo "  无改动，跳过提交"
  else
    git add -A
    git commit -m "fix: 发布级运行时修复（depends fabric→fabric-api / neoforge.mods.toml 版本范围对齐）

- fabric.mod.json: depends \"fabric\" 在 26.x/1.21/1.20 的 loader 下无候选
  （fabric-api 主 jar id 为 fabric-api，官方模板亦用 fabric-api）→ 改 \"fabric-api\": \"*\"
- neoforge.mods.toml: minecraft versionRange 从 1.21.11/26.3 复制残留写死，
  与 gradle.properties 的 minecraft_version_range 不一致 → 对齐为 ${RANGE}

Co-Authored-By: Claude Opus 5 (1M context) <noreply@anthropic.com>" >/dev/null
    git push origin "$b" 2>&1 | tail -1
  fi
done

echo "=== 全部完成"
