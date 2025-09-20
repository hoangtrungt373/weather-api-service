# Weather API Service - Configuration Guide

This document explains the different environment configurations available for the Weather API Service.

## Configuration Files

### 1. Base Configuration (`application.yml`)
- Contains common settings shared across all environments
- Sets the default active profile to `local`
- Defines common server, cache, and API configurations

### 2. Local Development (`application-local.yml`)
- **Profile**: `local`
- **Redis**: Connects to `localhost:6379`
- **Logging**: DEBUG level for detailed development logs
- **DevTools**: Enabled for hot reloading
- **Management**: All endpoints exposed for debugging

### 3. Docker Environment (`application-docker.yml`)
- **Profile**: `docker`
- **Redis**: Connects to `redis` service (Docker container name)
- **Logging**: INFO level with file logging to `/app/logs/`
- **DevTools**: Disabled
- **Management**: Limited endpoints for monitoring

### 4. Production Environment (`application-prod.yml`)
- **Profile**: `production`
- **Redis**: Configurable via environment variables
- **Logging**: WARN level with structured logging
- **Security**: SSL and CORS configuration
- **Management**: Minimal endpoint exposure

## Environment Variables

### Required for Production
```bash
WEATHER_API_KEY=your_api_key_here
REDIS_HOST=your_redis_host
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password  # Optional
```

### Optional Environment Variables
```bash
REDIS_DATABASE=0
REDIS_SSL=false
CORS_ALLOWED_ORIGINS=https://yourdomain.com
LOG_FILE_PATH=/var/log/weather-api-service/application.log
JAVA_OPTS=-Xms512m -Xmx1024m
```

## Running the Application

### Local Development
```bash
# Using Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Using script (Linux/Mac)
./scripts/start-local.sh

# Using script (Windows)
scripts\start-local.bat
```

### Docker Environment
```bash
# Using Docker Compose
docker-compose up --build

# Using script
./scripts/start-docker.sh
```

### Production
```bash
# Set environment variables
export WEATHER_API_KEY=your_key
export REDIS_HOST=your_redis_host

# Run with production profile
java -jar app.jar --spring.profiles.active=production

# Using script
./scripts/start-prod.sh
```

## Docker Configuration

### Dockerfile Features
- Multi-stage build for optimized image size
- Non-root user for security
- Health checks
- JVM container optimizations
- Log directory setup

### Docker Compose Services
- **weather-api**: Spring Boot application
- **redis**: Redis cache server
- **Network**: Isolated bridge network
- **Volumes**: Persistent Redis data and application logs

## Configuration Profiles

| Setting | Local | Docker | Production |
|---------|-------|--------|------------|
| Redis Host | localhost | redis | Environment variable |
| Logging Level | DEBUG | INFO | WARN |
| DevTools | Enabled | Disabled | Disabled |
| Management Endpoints | All | Limited | Minimal |
| File Logging | Console only | File + Console | File only |
| SSL | Disabled | Disabled | Configurable |

## Cache Configuration

All environments use the same cache TTL settings:
- **Current Weather**: 15 minutes
- **Forecast**: 12 hours  
- **Historical**: 7 days

## Health Checks

### Application Health
- **Endpoint**: `/actuator/health`
- **Local**: `http://localhost:8081/actuator/health`
- **Docker**: `http://localhost:8081/actuator/health`

### Redis Health
- Automatically checked by Spring Boot
- Connection pool monitoring
- Cache operation monitoring

## Monitoring and Logging

### Local Development
- Console logging with colored output
- Debug level for detailed information
- All management endpoints available

### Docker Environment
- File logging to `/app/logs/`
- INFO level logging
- Health and metrics endpoints

### Production
- Structured logging to specified file path
- WARN level logging
- Minimal endpoint exposure for security

## Troubleshooting

### Common Issues

1. **Redis Connection Failed**
   - Check if Redis is running
   - Verify host and port configuration
   - Check network connectivity

2. **API Key Issues**
   - Ensure `WEATHER_API_KEY` is set
   - Verify API key is valid
   - Check API rate limits

3. **Port Conflicts**
   - Default port is 8081
   - Change in `server.port` if needed
   - Check if port is already in use

4. **Docker Build Issues**
   - Ensure Dockerfile is in project root
   - Check .dockerignore file
   - Verify Maven build works locally

### Log Locations
- **Local**: Console output
- **Docker**: `./logs/weather-api-service.log`
- **Production**: `/var/log/weather-api-service/application.log`

## Security Considerations

### Production Security
- Use environment variables for sensitive data
- Enable SSL/TLS in production
- Configure CORS properly
- Use strong Redis passwords
- Limit management endpoint exposure
- Run as non-root user in containers
