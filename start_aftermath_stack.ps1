# Aftermath Complete Product Stack Launcher
$javaExe = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin\java.exe'
$rootDir = $PSScriptRoot

Write-Host "====================================================" -ForegroundColor Cyan
Write-Host "       STARTING AFTERMATH PRODUCT STACK             " -ForegroundColor Cyan
Write-Host "====================================================" -ForegroundColor Cyan

# 1. Start Collector Service (Port 8090)
Write-Host "[1/4] Starting Collector Service on http://localhost:8090..." -ForegroundColor Yellow
$collectorProc = Start-Process $javaExe -ArgumentList "-jar", "$rootDir\aftermath-collector\target\aftermath-collector-0.1.0-SNAPSHOT.jar" -PassThru

# 2. Start Coupon Service (Port 8081)
Write-Host "[2/4] Starting Coupon Microservice on http://localhost:8081..." -ForegroundColor Yellow
$couponProc = Start-Process $javaExe -ArgumentList "-jar", "$rootDir\sample-app\coupon-service\target\coupon-service-0.1.0-SNAPSHOT.jar" -PassThru

# 3. Start Payment Service (Port 8082)
Write-Host "[3/4] Starting Payment Microservice (with Capture SDK) on http://localhost:8082..." -ForegroundColor Yellow
$paymentProc = Start-Process $javaExe -ArgumentList "-jar", "$rootDir\sample-app\payment-service\target\payment-service-0.1.0-SNAPSHOT.jar" -PassThru

# 4. Start React Web Dashboard (Port 5173)
Write-Host "[4/4] Starting Web Dashboard on http://localhost:5173..." -ForegroundColor Yellow
$uiProc = Start-Process powershell -ArgumentList "-Command", "Set-Location '$rootDir\aftermath-ui'; npm run dev" -PassThru

Write-Host "`nWaiting 10 seconds for services to initialize..." -ForegroundColor LightGray
Start-Sleep -Seconds 10

Write-Host "`n====================================================" -ForegroundColor Green
Write-Host "       AFTERMATH STACK IS LIVE AND RUNNING!         " -ForegroundColor Green
Write-Host "====================================================" -ForegroundColor Green
Write-Host "Web Dashboard:      http://localhost:5173" -ForegroundColor White
Write-Host "Collector API:      http://localhost:8090/api/v1/incidents" -ForegroundColor White
Write-Host "Payment Service:    http://localhost:8082" -ForegroundColor White
Write-Host "Coupon Service:     http://localhost:8081" -ForegroundColor White
Write-Host "====================================================" -ForegroundColor Green

Write-Host "`nTo test live incident capture:" -ForegroundColor Cyan
Write-Host "Open PowerShell or Command Prompt and run:" -ForegroundColor Gray
Write-Host 'Invoke-RestMethod -Uri "http://localhost:8082/api/payments" -Method Post -Body ''{"amount":100,"couponCode":"PREMIUM50","customerId":"cust-100"}'' -ContentType "application/json" -Headers @{Authorization="Bearer secret-token-123"}' -ForegroundColor Yellow
Write-Host "`nThen refresh your browser at http://localhost:5173 to view the captured incident!" -ForegroundColor Green
