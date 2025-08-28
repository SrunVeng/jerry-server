# ---- Build stage ----
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine

# Default profile can be overridden by env at runtime
ENV SPRING_PROFILES_ACTIVE=prod
# Optional JVM flags (memory limits, GC, etc.)
ENV JAVA_OPTS=""

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
# Use sh -c so we can expand env vars
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} $JAVA_OPTS -jar app.jar"]
