# Use gradle Image
FROM gradle:8.12.1-jdk-21-and-23-corretto AS buildImage
WORKDIR /app
COPY . .
RUN gradle clean build

# Use OpenJDK base image
FROM amazoncorretto:21

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY --from=buildImage /app/build/libs/GitHubScoringRepository-*-SNAPSHOT.jar app.jar

# Expose the application port (change this if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]