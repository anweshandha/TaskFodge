# ==========================
# Stage 1: Build with Maven
# ==========================
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies offline
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests)
RUN mvn clean package -Dmaven.test.skip=true -B

# ==========================
# Stage 2: Runtime image
# ==========================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy wait-for-it script
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# Copy built JAR from build stage
ARG JAR_FILE=/workspace/target/*.jar
COPY --from=build ${JAR_FILE} /app/app.jar

# Expose port 8080
EXPOSE 8080

# JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m -Dspring.profiles.active=prod"

# Run app with wait-for-it to ensure dependencies are up
ENTRYPOINT ["sh", "-c", "/app/wait-for-it.sh postgres:5432 -- /app/wait-for-it.sh redis:6379 -- /app/wait-for-it.sh kafka:9092 -- java $JAVA_OPTS -jar /app/app.jar"]
