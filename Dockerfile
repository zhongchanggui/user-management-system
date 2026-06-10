FROM eclipse-temurin:8-jdk AS build
WORKDIR /app
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/user-management-system-1.0.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
