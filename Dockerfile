FROM openjdk:17-jdk-buster

WORKDIR /app

COPY build/libs/cryptocurrency-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
