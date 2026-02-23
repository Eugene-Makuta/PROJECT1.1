# Build stage
FROM maven:3.8.1-openjdk-11 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM maven:3.8.1-openjdk-11
WORKDIR /app
COPY --from=builder /app/target/lost-and-found-api-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
