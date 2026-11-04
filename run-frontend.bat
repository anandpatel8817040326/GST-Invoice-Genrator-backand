@echo off
title GST Invoice Generator - Frontend (React + Vite)
cd /d "%~dp0frontend"

echo ===================================================
echo   Starting GST Invoice Generator Frontend Portal
echo ===================================================
echo.

if not exist "node_modules" (
    echo [1/2] Installing frontend dependencies (First time setup)...
    call npm install
    if errorlevel 1 (
        echo Error installing dependencies. Please ensure Node.js is installed.
        pause
        exit /b 1
    )
) else (
    echo [1/2] Dependencies already installed.
)

echo.
echo [2/2] Launching Vite Dev Server on http://localhost:5173 ...
echo.
call npm run dev
pause

