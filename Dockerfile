# Dockerfile
# Stage 1: Build
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy pom.xml first (for better caching)
COPY pom.xml .

# Download all dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:17-slim
WORKDIR /app

# Install curl for health checks and PostgreSQL client for debugging
RUN apt-get update && \
    apt-get install -y curl postgresql-client && \
    rm -rf /var/lib/apt/lists/*

# Copy the JAR file
COPY --from=build /app/target/*.jar app.jar

# Create a health check script
RUN echo '#!/bin/bash\n\
curl -f http://localhost:8080/api/test/ping || exit 1' > /healthcheck.sh && \
    chmod +x /healthcheck.sh

# Expose port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD /healthcheck.sh

# Run the application with better JVM settings
ENTRYPOINT ["java", \
    "-Xmx512m", \
    "-Xms256m", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]