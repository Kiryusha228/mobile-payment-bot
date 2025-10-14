FROM gradle:jdk17-focal AS build

WORKDIR /app

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY .env .

COPY tgbot-microservice/src ./tgbot-microservice/src
COPY tgbot-microservice/build.gradle.kts ./tgbot-microservice/

RUN gradle :tgbot-microservice:build -x test --no-daemon

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/tgbot-microservice/build/libs/*.jar app.jar
COPY --from=build /app/.env .

CMD ["java", "-jar", "app.jar"]