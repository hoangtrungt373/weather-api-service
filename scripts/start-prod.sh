#!/bin/bash

# Production environment startup script
echo "Starting Weather API Service in PRODUCTION mode..."

# Validate required environment variables
if [ -z "$WEATHER_API_KEY" ]; then
    echo "ERROR: WEATHER_API_KEY environment variable is required"
    exit 1
fi

if [ -z "$REDIS_HOST" ]; then
    echo "ERROR: REDIS_HOST environment variable is required"
    exit 1
fi

# Set environment variables
export SPRING_PROFILES_ACTIVE=production

# Create logs directory
mkdir -p /var/log/weather-api-service

# Start the application
echo "Starting application with production configuration..."
java $JAVA_OPTS -jar app.jar

echo "Application started successfully!"
