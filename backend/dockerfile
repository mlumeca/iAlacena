FROM bellsoft/liberica-openjdk-alpine:17 AS build
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn 
COPY pom.xml .
COPY src src
RUN ./mvnw install -DskipTests

FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]