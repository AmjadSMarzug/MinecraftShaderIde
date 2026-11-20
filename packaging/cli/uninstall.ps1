# removes shader-build installed by install.ps1 (Windows)
$ErrorActionPreference = 'Stop'
$dest = Join-Path $env:LOCALAPPDATA 'Programs\shader-build'

$userPath = [Environment]::GetEnvironmentVariable('Path', 'User')
if ($userPath) {
    $parts = $userPath -split ';' | Where-Object { $_ -ne '' -and $_ -ne $dest }
    [Environment]::SetEnvironmentVariable('Path', ($parts -join ';'), 'User')
}
if (Test-Path $dest) { Remove-Item -Recurse -Force $dest }
Write-Host 'removed shader-build'
