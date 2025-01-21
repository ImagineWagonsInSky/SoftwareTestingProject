FROM ubuntu:latest
LABEL authors="guillermoperfect"

ENTRYPOINT ["top", "-b"]

# Build
FROM maven:3.8.5-openjdk-17 AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the jar file from the build stage
COPY --from=builder /app/target/ilp_submission_2-0.0.1-SNAPSHOT.jar /app/ilp_submission_2-0.0.1-SNAPSHOT.jar

EXPOSE 8080

# Run the Spring Boot app
CMD ["java", "-jar", "/app/ilp_submission_2-0.0.1-SNAPSHOT.jar"]
