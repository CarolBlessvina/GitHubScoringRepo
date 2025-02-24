# Use OpenJDK base image
FROM amazoncorretto:21

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/GitHubScoringRepository-*-SNAPSHOT.jar app.jar

# Expose the application port (change this if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]