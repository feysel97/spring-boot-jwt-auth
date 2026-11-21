# Use the official Eclipse Temurin Java 21 image
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy the compiled Spring Boot JAR
COPY build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]