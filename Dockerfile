# Stage 1: Build stage
FROM maven:3.9.0-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml from backend - using absolute path from root
COPY backend/pom.xml pom.xml

# Copy source code - using absolute path from root
COPY backend/src src

# Build the application
RUN mvn clean package -DskipTests

# Debug: List what was built
RUN ls -la /build/target/*.jar

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built JAR from builder stage - using specific filename
COPY --from=builder /build/target/event-management-system-1.0.0.jar app.jar

# Verify JAR exists
RUN ls -la /app/app.jar

# Create non-root user for security
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
USER appuser

# Expose port 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/health || exit 1

# Set environment variables for Railway
ENV SPRING_PROFILES_ACTIVE=production
ENV PORT=8080

# Run the application
CMD ["java", "-Dserver.port=8080", "-Dspring.profiles.active=production", "-jar", "app.jar"]
