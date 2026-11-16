
# Stage 1: Build application
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy complete microservices project
COPY . .

# Build only product-service and its required dependencies
RUN mvn -B -pl product-service -am clean package -DskipTests

# Stage 2: Run application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy generated JAR
COPY --from=build /app/product-service/target/*.jar app.jar

# Render assigns the port through PORT environment variable
ENV PORT=8080

EXPOSE 8080

# Start Spring Boot application
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
