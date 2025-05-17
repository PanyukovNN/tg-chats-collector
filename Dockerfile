FROM eclipse-temurin:17-jdk-jammy AS BUILD_IMAGE

COPY . ./

RUN ./gradlew build

# ---

FROM eclipse-temurin:17-jdk-jammy

# Создаем директории для нативных библиотек
RUN mkdir -p /app/lib
RUN mkdir -p /app/

# Устанавливаем необходимые зависимости
RUN apt-get update && apt-get install -y \
    openssl \
    zlib1g \
    libc++1 \
    && rm -rf /var/lib/apt/lists/*

COPY --from=BUILD_IMAGE ./build/libs/tg-chats-collector.jar /app/
COPY --from=BUILD_IMAGE ./build/libs/tdjni* /app/lib/
COPY --from=BUILD_IMAGE ./build/libs/libtdjni* /app/lib/

EXPOSE 8008
ENV TZ=Europe/Moscow

# Добавляем путь к библиотекам в LD_LIBRARY_PATH
ENV LD_LIBRARY_PATH=/app/lib:${LD_LIBRARY_PATH}

ENTRYPOINT ["java", "-Djava.library.path=/app/lib/", "-jar", "/app/tg-chats-collector.jar"]
