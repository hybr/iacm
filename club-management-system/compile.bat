@echo off
echo Compiling Club Management System...

if not exist build mkdir build

echo Compiling Java source files...
javac -cp "lib\sqlite-jdbc-3.45.0.0.jar;lib\slf4j-api-1.7.36.jar;lib\slf4j-simple-1.7.36.jar" -d build -sourcepath src\main\java src\main\java\com\clubmanagement\ClubManagementApp.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo To run the application, use: run.bat
) else (
    echo Compilation failed!
    pause
    exit /b 1
)