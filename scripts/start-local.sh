#!/bin/bash

# Local development startup script
echo "Starting Weather API Service in LOCAL mode..."

# Set environment variables
export SPRING_PROFILES_ACTIVE=local
export WEATHER_API_KEY=${WEATHER_API_KEY:-T9FM85UY3S5VGQWPGKZYQL5UR}

# Check if Redis is running
echo "Checking Redis connection..."
if ! nc -z localhost 6379; then
    echo "ERROR: Redis is not running on localhost:6379"
    echo "Please start Redis first:"
    echo "  - Using Docker: docker run -d -p 6379:6379 redis:7-alpine"
    echo "  - Using local installation: redis-server"
    exit 1
fi

echo "Redis is running. Starting application..."

# Start the application
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
