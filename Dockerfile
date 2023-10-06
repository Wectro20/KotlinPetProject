FROM openjdk:17-jdk-buster

WORKDIR /app

COPY build/libs/cryptocurrency-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application-docker.yml application.yml

ENTRYPOINT ["java", "-jar", "app.jar"]
