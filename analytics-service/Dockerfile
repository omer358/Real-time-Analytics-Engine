# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy only pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies (go-offline) – cached unless pom.xml changes
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the project using multiple threads
RUN mvn clean package -DskipTests -T 2C

# Run stage
FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*SNAPSHOT.jar ./analytics-service.jar

# Reduce JVM startup overhead
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Expose the port your app runs on
EXPOSE 8003

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar analytics-service.jar"]
