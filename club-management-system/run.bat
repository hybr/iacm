@echo off
echo Starting Club Management System...

java -cp "build;lib/*" com.clubmanagement.ClubManagementApp

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application finished with exit code: %ERRORLEVEL%
    echo.
    pause
)