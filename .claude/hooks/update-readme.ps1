# PostToolUse hook: git commit / git push 后，用 .claude/usage.json 汇总数据更新 README.md 的"开发统计"节
# 若 README 有更新且当前提交尚未推送，则自动 amend 进本次提交；已推送则仅暂存，提示下个提交带上
$ErrorActionPreference = 'Stop'

$inputJson = [Console]::In.ReadToEnd()
if ([string]::IsNullOrWhiteSpace($inputJson)) { exit 0 }

try {
    $data = $inputJson | ConvertFrom-Json
} catch { exit 0 }

$cmd = ''
if ($data.tool_input) { $cmd = [string]$data.tool_input.command }
if ([string]::IsNullOrWhiteSpace($cmd)) { exit 0 }
$isCommit = $cmd -match 'git\s+commit'
$isPush = $cmd -match 'git\s+push'
if (-not ($isCommit -or $isPush)) { exit 0 }

$projectDir = $env:CLAUDE_PROJECT_DIR
if (-not $projectDir) { $projectDir = (Get-Location).Path }
$usageFile = Join-Path $projectDir '.claude\usage.json'
$readmeFile = Join-Path $projectDir 'README.md'

if (-not (Test-Path $usageFile)) { exit 0 }
$records = @()
try { $records = @(Get-Content $usageFile -Raw -Encoding UTF8 | ConvertFrom-Json) } catch { exit 0 }
if ($records.Count -eq 0) { exit 0 }

# 汇总统计
$totalIn = 0L; $totalOut = 0L; $totalCache = 0L; $totalCost = 0.0
foreach ($r in $records) {
    $totalIn   += [long]($r.input_tokens + $r.cache_creation_input_tokens + $r.cache_read_input_tokens)
    $totalOut  += [long]$r.output_tokens
    $totalCache += [long]($r.cache_creation_input_tokens + $r.cache_read_input_tokens)
    if ($r.total_cost_usd) { $totalCost += [double]$r.total_cost_usd }
}
$sessions = $records.Count
$lastTs = $records[$records.Count - 1].ts
if ($lastTs) { $lastTs = ([datetime]$lastTs).ToString('yyyy-MM-dd HH:mm') }

$section = @(
    '## 开发统计'
    ''
    '| 指标 | 数值 |'
    '|---|---|'
    "| 累计会话数 | $sessions |"
    "| 累计输入 tokens（含缓存） | $totalIn |"
    "| 累计输出 tokens | $totalOut |"
    "| 累计缓存 tokens | $totalCache |"
    ("| 累计估算成本 | `$" + ('{0:N4}' -f $totalCost) + ' |')
    "| 最近更新 | $lastTs |"
    ''
) -join "`n"

# 替换或追加 README 中的统计节
if (-not (Test-Path $readmeFile)) { exit 0 }
$readme = Get-Content $readmeFile -Raw -Encoding UTF8
$startMarker = '## 开发统计'
$idx = $readme.IndexOf($startMarker)
if ($idx -ge 0) {
    # 找到下一级标题或文件末尾作为节边界
    $rest = $readme.Substring($idx + $startMarker.Length)
    $nextHead = $rest.IndexOf("`n## ")
    if ($nextHead -ge 0) {
        $readme = $readme.Substring(0, $idx) + $section + $rest.Substring($nextHead + 1)
    } else {
        $readme = $readme.Substring(0, $idx) + $section
    }
} else {
    $readme = $readme.TrimEnd() + "`n`n" + $section
}
[System.IO.File]::WriteAllText($readmeFile, $readme, (New-Object System.Text.UTF8Encoding($false)))

# 暂存改动（usage.json 与 README.md）
Push-Location $projectDir
try {
    git add '.claude/usage.json' 'README.md' | Out-Null
    $readmeChanged = git status --porcelain 'README.md'
    if ($readmeChanged) {
        if ($isCommit -and -not $isPush) {
            # 仅当本次是 commit 且该提交尚未推送时才 amend（已推送则 amend 会导致下次 push 需 --force）
            $pushed = $false
            try {
                $head = (git rev-parse HEAD).Trim()
                $up = (git rev-parse '@{upstream}').Trim()
                if ($head -eq $up) { $pushed = $true }
            } catch { }
            if (-not $pushed) {
                git commit -q --amend --no-edit | Out-Null
            }
        }
    }
} finally {
    Pop-Location
}
exit 0
