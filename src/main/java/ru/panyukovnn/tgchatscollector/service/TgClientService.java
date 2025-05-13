package ru.panyukovnn.tgchatscollector.service;

import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.tgchatscollector.dto.ChatHistoryResponse;
import ru.panyukovnn.tgchatscollector.dto.TgMessageDto;
import ru.panyukovnn.tgchatscollector.property.TgChatLoaderProperty;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgClientService {

    private final SimpleTelegramClient tgClient;
    private final TgChatLoaderProperty tgChatLoaderProperty;

    public Long findChatIdByPublicChatName(String publicChatName) {
        TdApi.SearchPublicChat searchPublicChat = new TdApi.SearchPublicChat(publicChatName);

        try {
            return tgClient.send(searchPublicChat)
                .thenApply(it -> it.id)
                .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getChatNameById(Long chatId) {
        try {
            return tgClient.send(new TdApi.GetChat(chatId))
                .thenApply(it -> it.title)
                .exceptionally(e -> {
                    log.error("Ошибка при определении имени чата по идентификатору: {}. Сообщение об ошибке: {}", chatId, e.getMessage(), e);

                    throw new RuntimeException(e);
                })
                .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TgMessageDto> collectAllMessagesFromPublicChat(Long chatId, @Nullable Integer limit, @Nullable Integer daysLimit) {
        List<TgMessageDto> messageDtos = new ArrayList<>();
        long fromMessageId = 0L;
        LocalDateTime dateBound = LocalDateTime.now(ZoneOffset.UTC)
            .minusDays(daysLimit != null
                ? daysLimit
                : tgChatLoaderProperty.defaultDaysLimit());

        int limitMessagesToLoad = limit == null ? tgChatLoaderProperty.defaultMessagesLimit() : limit;

        while (messageDtos.size() < limitMessagesToLoad) {
            TdApi.Messages messages = collectPublicChatMessages(chatId, fromMessageId);

            for (TdApi.Message message : messages.messages) {
                TdApi.MessageContent content = message.content;

                if (content instanceof TdApi.MessageText messageText) {
                    LocalDateTime messageDateTimeUtc = LocalDateTime.ofEpochSecond(message.date, 0, ZoneOffset.UTC);
                    LocalDateTime messageDateTime = messageDateTimeUtc
                        .plusHours(3); // Московское время

                    if (messageDateTimeUtc.isBefore(dateBound)) {
                        return messageDtos;
                    }

                    messageDtos.add(TgMessageDto.builder()
                        .dateTime(messageDateTime)
                        .text(messageText.text.text)
                        .build());
                }

                log.info("Загружено сообщений в пачке: {}", messages.messages.length);

                if (messages.messages.length == 0) {
                    return messageDtos;
                }

                fromMessageId = messages.messages[messages.messages.length - 1].id;
            }

            log.info("Извлечено сообщений: {}", messageDtos.size());
        }

        return messageDtos;
    }

    private TdApi.Messages collectPublicChatMessages(Long chatId, long fromMessageId) {
        TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, fromMessageId, 0, 100, false);

        try {
            return tgClient.send(getChatHistory)
                .exceptionally(e -> {
                    log.error("Ошибка при выгрузке истории чата: {}", e.getMessage(), e);

                    throw new RuntimeException(e);
                })
                .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
