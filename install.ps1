# AFTERMATH 1-Click Installer for Windows
# Installs aftermath-cli into user environment PATH

$ErrorActionPreference = "Stop"

Write-Host "==========================================================================" -ForegroundColor Cyan
Write-Host "⚡ AFTERMATH 1-CLICK CLI INSTALLER (WINDOWS)" -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan

$installDir = "$env:USERPROFILE\.aftermath\bin"
if (!(Test-Path -Path $installDir)) {
    New-Item -ItemType Directory -Path $installDir -Force | Out-Null
    Write-Host "Created installation directory: $installDir" -ForegroundColor Green
}

$cliJarSource = "$PSScriptRoot\aftermath-cli\target\aftermath-cli-0.1.0-SNAPSHOT.jar"
$cliJarDest = "$installDir\aftermath-cli.jar"

if (Test-Path -Path $cliJarSource) {
    Copy-Item -Path $cliJarSource -Destination $cliJarDest -Force
    Write-Host "Installed aftermath-cli.jar to $cliJarDest" -ForegroundColor Green
} else {
    Write-Host "⚠️ Warning: aftermath-cli.jar not found at $cliJarSource. Please run 'mvn clean package' first." -ForegroundColor Yellow
}

$cmdWrapper = "$installDir\aftermath.cmd"
$cmdContent = "@echo off`r`njava -jar `"$installDir\aftermath-cli.jar`" %*"
Set-Content -Path $cmdWrapper -Value $cmdContent
Write-Host "Created executable wrapper: $cmdWrapper" -ForegroundColor Green

# Add to User PATH if not present
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")
if ($userPath -notlike "*$installDir*") {
    [Environment]::SetEnvironmentVariable("Path", "$userPath;$installDir", "User")
    Write-Host "✅ Added $installDir to User PATH environment variable." -ForegroundColor Green
    Write-Host "Please restart your PowerShell or CMD terminal for PATH changes to take effect." -ForegroundColor Yellow
} else {
    Write-Host "ℹ️  $installDir is already in User PATH." -ForegroundColor Gray
}

Write-Host "`n==========================================================================" -ForegroundColor Cyan
Write-Host "🚀 INSTALLATION COMPLETE!" -ForegroundColor Green
Write-Host "Run 'aftermath status' or 'aftermath attach --path .' in any project folder!" -ForegroundColor Green
Write-Host "==========================================================================" -ForegroundColor Cyan
