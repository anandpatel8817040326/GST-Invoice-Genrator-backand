
# Stage 1: Build application
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy complete Maven project
COPY . .

# Build payment service
RUN mvn -B -pl payment-service -am clean package -DskipTests

# Stage 2: Run application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy executable JAR
COPY --from=build /app/payment-service/target/payment-service.jar app.jar

# Render dynamic port
ENV PORT=8080

EXPOSE 8080

# Start application
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
