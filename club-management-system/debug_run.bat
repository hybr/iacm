@echo off
echo =========================================
echo Club Management System Debug Launcher
echo =========================================
echo.

echo Current directory: %CD%
echo.

echo Checking files and directories:
echo.

echo Build directory:
if exist build (
    echo ✓ build directory exists
    dir build /b | findstr "com" >nul
    if %ERRORLEVEL% EQU 0 (
        echo ✓ com package directory found
    ) else (
        echo ✗ com package directory not found
    )
) else (
    echo ✗ build directory not found
)

echo.
echo SQLite JDBC driver:
if exist lib\sqlite-jdbc-3.45.0.0.jar (
    echo ✓ lib\sqlite-jdbc-3.45.0.0.jar exists
) else (
    echo ✗ lib\sqlite-jdbc-3.45.0.0.jar not found
)

echo.
echo Main class check:
if exist build\com\clubmanagement\ClubManagementApp.class (
    echo ✓ ClubManagementApp.class found
) else (
    echo ✗ ClubManagementApp.class not found
)

echo.
echo Classpath: build;lib\sqlite-jdbc-3.45.0.0.jar;lib\slf4j-api-1.7.36.jar;lib\slf4j-simple-1.7.36.jar
echo Command: java -cp "build;lib\sqlite-jdbc-3.45.0.0.jar;lib\slf4j-api-1.7.36.jar;lib\slf4j-simple-1.7.36.jar" com.clubmanagement.ClubManagementApp
echo.

echo Starting application...
echo =========================================
echo.

java -cp "build;lib\sqlite-jdbc-3.45.0.0.jar;lib\slf4j-api-1.7.36.jar;lib\slf4j-simple-1.7.36.jar" com.clubmanagement.ClubManagementApp

echo.
echo =========================================
echo Application finished with exit code: %ERRORLEVEL%
pause