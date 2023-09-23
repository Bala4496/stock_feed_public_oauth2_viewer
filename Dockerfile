# Building the Spring Boot JAR with Gradle
FROM gradle:8.2.1-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle clean build -x test

# Creating the Docker image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/stock_feed_public_oauth2_viewer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9000
CMD ["java", "-jar", "app.jar"]
