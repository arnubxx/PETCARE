# Multi-stage build for Railway deployment
# Stage 1: Build the application with Maven
FROM maven:3.8.6-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the WAR file
RUN mvn clean package -DskipTests

# Stage 2: Deploy to Tomcat
FROM tomcat:11-jdk17

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from builder stage
COPY --from=builder /app/target/PETCARE-1.0.0.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
