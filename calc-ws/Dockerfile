# Use a standard, lightweight Java base image
FROM amazoncorretto:21-alpine-jdk

# Set a working directory inside the container
WORKDIR /app

# Copy our compiled Java application (the "fat jar") into the container
# Your build tool (Maven/Gradle) should be configured to produce this single jar
COPY target/calc-ws-0.0.1-SNAPSHOT.jar app.jar

# Tell Docker what command to run when the container starts
# This runs our web service
CMD ["java", "-jar", "app.jar"]