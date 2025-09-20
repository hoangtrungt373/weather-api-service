@echo off
REM Local development startup script for Windows

echo Starting Weather API Service in LOCAL mode...

REM Set environment variables
set SPRING_PROFILES_ACTIVE=local
set WEATHER_API_KEY=%WEATHER_API_KEY%
if "%WEATHER_API_KEY%"=="" set WEATHER_API_KEY=T9FM85UY3S5VGQWPGKZYQL5UR

REM Check if Redis is running
echo Checking Redis connection...
netstat -an | find "6379" >nul
if %errorlevel% neq 0 (
    echo ERROR: Redis is not running on localhost:6379
    echo Please start Redis first:
    echo   - Using Docker: docker run -d -p 6379:6379 redis:7-alpine
    echo   - Using local installation: redis-server
    pause
    exit /b 1
)

echo Redis is running. Starting application...

REM Start the application
mvnw spring-boot:run -Dspring-boot.run.profiles=local

pause
