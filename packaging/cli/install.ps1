# installs shader-build so it runs from any folder (Windows)
# run: powershell -ExecutionPolicy Bypass -File install.ps1
$ErrorActionPreference = 'Stop'
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$dest = Join-Path $env:LOCALAPPDATA 'Programs\shader-build'

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Warning 'java is not installed. shader-build needs Java 21 or newer.'
}

New-Item -ItemType Directory -Force -Path $dest | Out-Null
Copy-Item (Join-Path $here 'shader-build.jar') $dest -Force
Copy-Item (Join-Path $here 'shader-build.cmd') $dest -Force

$userPath = [Environment]::GetEnvironmentVariable('Path', 'User')
$parts = @()
if ($userPath) { $parts = $userPath -split ';' | Where-Object { $_ -ne '' } }
if ($parts -notcontains $dest) {
    $newPath = (@($parts) + $dest) -join ';'
    [Environment]::SetEnvironmentVariable('Path', $newPath, 'User')
}

Write-Host "installed to $dest"
Write-Host 'open a new terminal and run: shader-build --help'
