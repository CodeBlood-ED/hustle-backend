# ==========================================
# Multi-stage Dockerfile for Hustle Backend
# ==========================================

# ------------------------------------------
# Stage 1: Build the Application
# ------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Cache maven dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source and build the executable jar
COPY src ./src
RUN mvn clean package -DskipTests

# ------------------------------------------
# Stage 2: Minimal Runtime Container
# ------------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-privileged system user for security
RUN addgroup -S hustle && adduser -S hustle -G hustle

# Copy the built jar from the builder stage
COPY --from=builder /build/target/hustle-backend-*.jar app.jar

# Set ownership
RUN chown -R hustle:hustle /app

USER hustle

# Expose Spring Boot server port
EXPOSE 8080

# Environment variables with sensible defaults
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m"

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
