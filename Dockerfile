# Use an official Maven image to build the app
FROM maven:3.9.9-eclipse-temurin-22-alpine AS build

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY java ./java
COPY sql ./sql

RUN mvn clean package

# Use a smaller image for the final application
FROM eclipse-temurin:22-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/app-updater-1.0.0-jar-with-dependencies.jar /app/app-updater.jar

# Copy the config
COPY config/config.properties /app/config.properties

# Copy the script
COPY run.sh /app/run.sh

RUN chmod +x /app/run.sh

# Create a cron job to run the script every 5 minutes
# The `>> /var/log/cron.log 2>&1` redirects stdout and stderr to a log file inside the container
# This is useful for debugging.
# The `crontab -` command reads the cron job from standard input.
RUN echo "18 0 * * * /app/run.sh >> /var/log/cron.log 2>&1" | crontab -

# Create the log file and ensure it's writable by cron (optional, but good practice)
RUN touch /var/log/cron.log && chmod 644 /var/log/cron.log

# Set the entrypoint to start the cron daemon in the foreground
# -f: Runs cron in the foreground (essential for Docker containers to keep running)
# -L /var/log/cron.log: Logs cron daemon activity to the specified file
CMD ["crond", "-f", "-L", "/var/log/cron.log"]
