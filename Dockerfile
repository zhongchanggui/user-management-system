FROM maven:3.8-eclipse-temurin-8 AS build

# Install Node.js 16 for frontend build
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_16.x | bash - && \
    apt-get install -y nodejs

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -Pprod -DskipTests -B

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/user-management-system-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--server.port=${PORT:-8080}"]
