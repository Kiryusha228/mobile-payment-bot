FROM gradle:jdk17-focal AS build

WORKDIR /app

COPY build.gradle.kts build.gradle.kts
COPY settings.gradle.kts settings.gradle.kts
COPY .env .env

COPY crud-microservice/src/main/resources crud-microservice/src/main/resources
COPY crud-microservice/src crud-microservice/src
COPY crud-microservice/build.gradle.kts crud-microservice/build.gradle.kts

RUN gradle crud-microservice:build --no-daemon

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/crud-microservice/build/libs/*.jar app/crud-microservice/app.jar
COPY --from=build /app/.env .env
COPY --from=build /app/crud-microservice/src/main/resources app/resources

CMD ["java", "-jar", "app/crud-microservice/app.jar"]
