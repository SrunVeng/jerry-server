FROM ubuntu:latest AS build
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk curl unzip git && \
    apt-get clean
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]