FROM maven:3.9-eclipse-temurin-17 AS build

ARG SERVICE
WORKDIR /app
COPY . .
RUN mvn -pl ${SERVICE} -am -DskipTests package

FROM eclipse-temurin:17-jre

ARG SERVICE
WORKDIR /app
COPY --from=build /app/${SERVICE}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
