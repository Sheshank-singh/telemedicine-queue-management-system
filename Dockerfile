# ==============================================================================
# Dockerfile for Telemedicine Queue Management System (TQMS)
# Base Image: Eclipse Temurin OpenJDK 17 JRE (Ubuntu Jammy)
# Author: Sheshank Singh (Roll: 23102A0017)
# ==============================================================================

FROM eclipse-temurin:17-jre-jammy

# Set container working directory
WORKDIR /app

# Metadata labels
LABEL maintainer="sheshank.singh@vit.edu"
LABEL description="DevOps-Enabled Telemedicine Queue Management System (TQMS)"
LABEL version="1.0.0"

# Copy pre-packaged executable WAR artifact from Maven target
COPY target/tqms.war app.war

# Expose internal Spring Boot application port
EXPOSE 8081

# Configure default environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8081

# Health check endpoint for Docker daemon
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Execute standalone Spring Boot embedded container
ENTRYPOINT ["java", "-jar", "app.war"]
