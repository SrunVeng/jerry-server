FROM ghcr.io/graalvm/jdk-community:21
WORKDIR app
ADD ./build/libs/jerry-1.0.0.jar /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar" , "-Dspring.profiles.active=local", "/app/mbankingloan-1.0.0.jar"]