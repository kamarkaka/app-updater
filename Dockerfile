# Use an official Maven image to build the app
FROM maven:3.9.9-eclipse-temurin-22-alpine AS build

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY java ./java

# Build the application
ARG DB_HOST
ARG DB_PORT
ARG DB_NAME
ARG DB_USER
ARG DB_PASSWORD

RUN echo "db: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
RUN echo "db user: ${DB_USER}"
RUN mvn clean package

# Use a smaller image for the final application
FROM eclipse-temurin:22-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/app-updater-1.0.0-jar-with-dependencies.jar /app/app-updater.jar

# Copy the config
COPY config/config.properties /app/config.properties

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "/app/app-updater.jar", "com.kamarkaka.appupdater.RunApp",
            "--config", "/app/config.properties",
            "--output", "/output/",
            "--send-email"]
