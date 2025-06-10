# Use Maven to build the JAR
FROM maven:3.8.4-eclipse-temurin-17 AS build

# Set work directory inside Docker container
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# Final image with JAR only
FROM eclipse-temurin:17-jdk-alpine

# Set working dir
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
