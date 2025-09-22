@echo off
echo Compiling Club Management System...

if not exist build mkdir build

echo Compiling Java source files...

REM Compile in dependency order
javac -cp "lib/*" -d build src\main\java\com\clubmanagement\models\*.java
javac -cp "lib/*;build" -d build src\main\java\com\clubmanagement\database\*.java
javac -cp "lib/*;build" -d build src\main\java\com\clubmanagement\dao\*.java
javac -cp "lib/*;build" -d build src\main\java\com\clubmanagement\security\*.java
javac -cp "lib/*;build" -d build src\main\java\com\clubmanagement\services\*.java
javac -cp "lib/*;build" -d build src\main\java\com\clubmanagement\gui\theme\*.java
javac -cp "lib/*;build" -d build src\main\java\com\clubmanagement\gui\components\*.java
javac -cp "lib/*;build" -d build src\main\java\com\clubmanagement\gui\*.java
javac -cp "lib/*;build" -d build src\main\java\com\clubmanagement\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo To run the application, use: run.bat
) else (
    echo Compilation failed!
    pause
    exit /b 1
)