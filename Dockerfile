FROM amazoncorretto:17.0.7-alpine AS BUILD_IMAGE

COPY . ./

RUN ./gradlew build

# ---

FROM amazoncorretto:17.0.7-alpine

COPY --from=BUILD_IMAGE ./build/libs/tg-chats-collector.jar /tg-chats-collector.jar

EXPOSE 8008
ENV TZ=Europe/Moscow

ENTRYPOINT ["java", "-jar", "/tg-chats-collector.jar"]