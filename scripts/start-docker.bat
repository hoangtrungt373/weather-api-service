@echo off
REM Docker environment startup script for Windows

echo Starting Weather API Service in DOCKER mode...

REM Set environment variables
set SPRING_PROFILES_ACTIVE=docker
set WEATHER_API_KEY=%WEATHER_API_KEY%
if "%WEATHER_API_KEY%"=="" set WEATHER_API_KEY=T9FM85UY3S5VGQWPGKZYQL5UR

REM Create logs directory
if not exist logs mkdir logs

REM Start services with Docker Compose
echo Starting services with Docker Compose...
docker-compose up --build

echo Services started successfully!
echo Weather API Service: http://localhost:8081
echo Redis: localhost:6379
echo Health Check: http://localhost:8081/actuator/health

pause
