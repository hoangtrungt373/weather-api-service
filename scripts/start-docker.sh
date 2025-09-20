#!/bin/bash

# Docker environment startup script
echo "Starting Weather API Service in DOCKER mode..."

# Set environment variables
export SPRING_PROFILES_ACTIVE=docker
export WEATHER_API_KEY=${WEATHER_API_KEY:-T9FM85UY3S5VGQWPGKZYQL5UR}

# Create logs directory
mkdir -p logs

# Start services with Docker Compose
echo "Starting services with Docker Compose..."
docker-compose up --build

echo "Services started successfully!"
echo "Weather API Service: http://localhost:8081"
echo "Redis: localhost:6379"
echo "Health Check: http://localhost:8081/actuator/health"
