#!/bin/bash

# Конфигурация
SSH_CONFIG=nvpnt
REMOTE_DIR=tg-chats-collector

# Проверка на наличие файлов рядом со скриптом
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"
ENV_FILE="$SCRIPT_DIR/config.env"
TDLIGHT_DB_PATH="$SCRIPT_DIR/../tdlight-session"

if [[ ! -f "$COMPOSE_FILE" || ! -f "$ENV_FILE" || ! -d "$TDLIGHT_DB_PATH" ]]; then
  echo "Ошибка: не найден docker-compose.yml или config.env рядом со скриптом, или бд телеграм по пути ../tdlight-session"
  exit 1
fi

echo "Создание папки $REMOTE_DIR на сервере (если не существует)..."
ssh "$SSH_CONFIG" "mkdir -p $REMOTE_DIR"

echo "Копирование файлов на сервер..."
scp "$COMPOSE_FILE" "$ENV_FILE" "$SSH_CONFIG:$REMOTE_DIR/"

echo "Копирование бд телеграм на сервер..."
scp -r "$TDLIGHT_DB_PATH" "$SSH_CONFIG:$REMOTE_DIR/tdlight-session"

echo "Готово. Файлы успешно отправлены."