# Stage 1: Build the Spring Boot .jar inside a Maven + Java 21 container
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml first and download dependencies (cached layer for faster rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and package the application (skipping tests for speed)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the compiled .jar inside a lightweight Java 21 JRE container
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the built .jar from Stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]