# Step 1: Use Debian as the base image
FROM debian:bullseye-slim as build

# Step 2: Install OpenJDK 17 and Maven
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven && \
    rm -rf /var/lib/apt/lists/*

# Check Java and Maven version for debugging
RUN java -version
RUN mvn -v

# Step 3: Set the working directory
WORKDIR /app

# Step 4: Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src ./src

# Check if the files are copied correctly
RUN ls -l /app

# Step 5: Build the application using Maven
RUN mvn clean package -DskipTests
RUN ls -l /app/target/

# Step 6: Use a smaller base image to run the application
FROM openjdk:17-jdk-slim

# Step 7: Set the working directory
WORKDIR /app

# Step 8: Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Step 9: Expose the port the app runs on
EXPOSE 8080

# Step 10: Ensure that the Spring Boot app starts
ENTRYPOINT ["java", "-jar", "app.jar"]