# Use the openjdk 17 image as base
FROM openjdk:17-jdk-buster

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/cryptocurrency-0.0.1-SNAPSHOT.jar app.jar

# Set the entrypoint for the container
ENTRYPOINT ["java", "-jar", "app/app.jar"]
