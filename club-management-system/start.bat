@echo off
title Club Management System
echo Starting Club Management System...
echo.

rem Change to the script directory
cd /d "%~dp0"

echo Current directory: %CD%
echo.

echo Checking build directory...
if exist build (
    echo ✓ Build directory found
) else (
    echo ✗ Build directory not found
    echo Please ensure the application is compiled first
    pause
    exit /b 1
)

echo Checking SQLite JDBC driver...
if exist lib\sqlite-jdbc-3.45.0.0.jar (
    echo ✓ SQLite JDBC driver found
) else (
    echo ✗ SQLite JDBC driver not found
    echo Please ensure sqlite-jdbc-3.45.0.0.jar is in the lib\ directory
    pause
    exit /b 1
)

echo.
echo Launching application...
echo.

java -cp "build;lib\sqlite-jdbc-3.45.0.0.jar;lib\slf4j-api-1.7.36.jar;lib\slf4j-simple-1.7.36.jar" com.clubmanagement.ClubManagementApp

echo.
echo Application closed.
pause