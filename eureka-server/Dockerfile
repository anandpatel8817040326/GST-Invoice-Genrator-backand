
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn -pl eureka-server -am clean package -DskipTests

RUN echo "Generated JAR files:" && \
    find /app/eureka-server/target -maxdepth 1 -name "*.jar" -print

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/eureka-server/target/eureka-server-*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
