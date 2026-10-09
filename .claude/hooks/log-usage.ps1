# Stop hook: 将会话结束时的 token 用量追加到项目 .claude/usage.json（独立统计，随仓库走）
$ErrorActionPreference = 'Stop'

$inputJson = [Console]::In.ReadToEnd()
if ([string]::IsNullOrWhiteSpace($inputJson)) { exit 0 }

try {
    $data = $inputJson | ConvertFrom-Json
} catch { exit 0 }

if (-not $data.usage) { exit 0 }

$projectDir = $env:CLAUDE_PROJECT_DIR
if (-not $projectDir) { $projectDir = (Get-Location).Path }
$usageFile = Join-Path $projectDir '.claude\usage.json'

$record = [ordered]@{
    ts                           = (Get-Date).ToString('o')
    session_id                   = $data.session_id
    input_tokens                 = $data.usage.input_tokens
    output_tokens                = $data.usage.output_tokens
    cache_creation_input_tokens  = $data.usage.cache_creation_input_tokens
    cache_read_input_tokens      = $data.usage.cache_read_input_tokens
    total_cost_usd               = $data.usage.total_cost_usd
}

$records = New-Object System.Collections.ArrayList
if (Test-Path $usageFile) {
    try {
        $existing = Get-Content $usageFile -Raw -Encoding UTF8 | ConvertFrom-Json
        if ($existing) { foreach ($r in $existing) { [void]$records.Add($r) } }
    } catch { }
}
[void]$records.Add([PSCustomObject]$record)
$json = $records | ConvertTo-Json -Depth 6
[System.IO.File]::WriteAllText($usageFile, $json, (New-Object System.Text.UTF8Encoding($false)))
exit 0
