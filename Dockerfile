# Step 1: Build the JAR with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR file
RUN mvn clean package -DskipTests

# Step 2: Run the app with a smaller JDK image
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built JAR into runtime image
COPY --from=builder /app/target/connektx-backend.jar app.jar

# Use Render's PORT environment variable
ENV PORT=8080
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
