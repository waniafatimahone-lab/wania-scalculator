@echo off
title Wania - Lavender Scientific Calculator
echo ===================================================
echo        Launching Wania (Lavender Edition)...
echo ===================================================

if not exist bin mkdir bin

echo Compiling WaniaCalculator.java...
javac -d bin -encoding UTF-8 WaniaCalculator.java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Ensure Java JDK is installed and in your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo Starting Wania Scientific Calculator...
java -cp bin wanialavender.WaniaCalculator
pause
