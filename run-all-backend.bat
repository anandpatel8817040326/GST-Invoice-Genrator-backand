@echo off
title GST Invoice Generator - Microservices Launcher
setlocal EnableDelayedExpansion

set "MVN_PATH=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
if not exist "!MVN_PATH!" (
    set "MVN_PATH=mvn"
)

:: Optimized memory flags for lightweight execution
set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1"

echo ============================================================
echo   GST Invoice Generator - Microservices Launcher
echo ============================================================
echo.
echo Step 1: Starting Eureka Server (Registry - Port 8761)...
start "Eureka Server (8761)" cmd /c "cd /d "%~dp0eureka-server" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"

echo Waiting 10 seconds for Eureka Server to initialize...
timeout /t 10 /nobreak >nul

echo Step 2: Starting API Gateway (Entry Point - Port 8080)...
start "API Gateway (8080)" cmd /c "cd /d "%~dp0api-gateway" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"

echo Waiting 5 seconds...
timeout /t 5 /nobreak >nul

echo Step 3: Starting Auth Service (Port 8081)...
start "Auth Service (8081)" cmd /c "cd /d "%~dp0auth-service" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"

echo Step 4: Starting Core Business Services...
start "Business Service (8082)" cmd /c "cd /d "%~dp0business-service" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"
start "Customer Service (8083)" cmd /c "cd /d "%~dp0customer-service" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"
start "Product Service (8084)" cmd /c "cd /d "%~dp0product-service" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"
start "GST Service (8086)" cmd /c "cd /d "%~dp0gst-service" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"
start "Invoice Service (8085)" cmd /c "cd /d "%~dp0invoice-service" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"
start "PDF Service (8088)" cmd /c "cd /d "%~dp0pdf-service" && set "MAVEN_OPTS=-XX:+UseSerialGC -Xmx128m -Xms32m -XX:TieredStopAtLevel=1" && "!MVN_PATH!" spring-boot:run"

echo.
echo ============================================================
echo  Core backend services are launching in individual windows!
echo  Eureka Dashboard: http://localhost:8761
echo  API Gateway:      http://localhost:8080
echo ============================================================
echo.
