FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/gpt-webhook-service/build/libs/*.jar /app/chat-app-gpt.jar
ENTRYPOINT ["java","-jar","/app/chat-app-gpt.jar","-config=local.conf"]