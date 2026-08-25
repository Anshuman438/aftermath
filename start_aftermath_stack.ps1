<#
.SYNOPSIS
    Starts the full Aftermath ecosystem (Collector, Coupon Service, Payment Service, Web UI).
#>

$javaExe = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin\java.exe'
$mavenCmd = 'C:\Users\Singh\maven\apache-maven-3.9.9\bin\mvn.cmd'
$root = 'C:\Users\Singh\Desktop\fail2test'

Set-Location $root

Write-Host "==========================================================================" -ForegroundColor Cyan
Write-Host "                AFTERMATH FULL STACK ORCHESTRATOR                         " -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan

Write-Host "`n[1/4] Building Maven Monorepo..." -ForegroundColor Yellow
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot'
& $mavenCmd clean package -f "$root\pom.xml" -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed! Stopping orchestration."
    exit 1
}

Write-Host "`n[2/4] Starting Services..." -ForegroundColor Yellow
Write-Host "  - Starting Collector Service (Port 8090)..."
$collector = Start-Process $javaExe -ArgumentList "-jar", "$root\aftermath-collector\target\aftermath-collector-0.1.0-SNAPSHOT.jar" -WorkingDirectory $root -PassThru

Write-Host "  - Starting Coupon Service (Port 8081)..."
$coupon = Start-Process $javaExe -ArgumentList "-jar", "$root\sample-app\coupon-service\target\coupon-service-0.1.0-SNAPSHOT.jar" -WorkingDirectory $root -PassThru

Write-Host "  - Starting Payment Service (Port 8082)..."
$payment = Start-Process $javaExe -ArgumentList "-jar", "$root\sample-app\payment-service\target\payment-service-0.1.0-SNAPSHOT.jar" -WorkingDirectory $root -PassThru

Write-Host "`nWaiting 20 seconds for Java services to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

Write-Host "`n[3/4] Starting Aftermath Web Dashboard UI..." -ForegroundColor Yellow
$ui = Start-Process "npm.cmd" -ArgumentList "run", "dev" -WorkingDirectory "$root\aftermath-ui" -PassThru

Write-Host "`n==========================================================================" -ForegroundColor Green
Write-Host "            AFTERMATH SYSTEM IS ONLINE AND READY!                         " -ForegroundColor Green
Write-Host "==========================================================================" -ForegroundColor Green
Write-Host "  * Web Dashboard:    http://localhost:5173" -ForegroundColor White
Write-Host "  * Collector API:     http://localhost:8090/api/v1/incidents" -ForegroundColor White
Write-Host "  * Payment Service:   http://localhost:8082/api/payments" -ForegroundColor White
Write-Host "  * Coupon Service:    http://localhost:8081/api/coupons/SAVE10" -ForegroundColor White
Write-Host "==========================================================================" -ForegroundColor Green
Write-Host "`nPress CTRL+C in this window or close it to stop all background services."

try {
    while ($true) {
        Start-Sleep -Seconds 2
    }
} finally {
    Write-Host "`nShutting down Aftermath services..." -ForegroundColor Red
    Stop-Process -Id $collector.Id -Force -ErrorAction SilentlyContinue
    Stop-Process -Id $coupon.Id -Force -ErrorAction SilentlyContinue
    Stop-Process -Id $payment.Id -Force -ErrorAction SilentlyContinue
    Stop-Process -Id $ui.Id -Force -ErrorAction SilentlyContinue
}