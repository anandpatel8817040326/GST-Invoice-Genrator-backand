
# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn -B -pl invoice-service -am clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/invoice-service/target/invoice-service.jar app.jar

ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
