# Base image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy the JAR file from target directory and rename it to app.jar
COPY target/*.jar user-service.jar

# Expose the port your application uses (change 8080 if needed)
EXPOSE 8081
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar user-service.jar"]
