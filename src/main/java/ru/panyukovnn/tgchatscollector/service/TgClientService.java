package ru.panyukovnn.tgchatscollector.service;

import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.jni.TdApi;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.tgchatscollector.dto.TgMessageDto;
import ru.panyukovnn.tgchatscollector.exception.TgChatsCollectorException;
import ru.panyukovnn.tgchatscollector.property.TgChatLoaderProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgClientService {

    private final SimpleTelegramClient tgClient;
    private final TgChatLoaderProperty tgChatLoaderProperty;

    public TdApi.Chat findChat(Long chatId, String publicChatName) {
        if (chatId == null && publicChatName == null) {
            throw new TgChatsCollectorException("46ea", "Отсутствуют chatId и chatName для идентификации чата");
        }

        try {
            CompletableFuture<TdApi.Chat> chatCompletableFuture = chatId != null
                ? tgClient.send(new TdApi.GetChat(chatId))
                : tgClient.send(new TdApi.SearchPublicChat(publicChatName));

            return chatCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new TgChatsCollectorException("9a1d",
                "Ошибка при поиске чата по идентификатору: %s. Или имени: %s. Сообщение об ошибке: %s"
                    .formatted(chatId, publicChatName, e.getMessage()),
                e);
        }
    }

    /**
     * Важно, чтобы даты передавались в UTC
     *
     * @param chatId идентификатор чата
     * @param limit предельное количество сообщений
     * @param dateFrom дата начала периода в UTC
     * @param dateTo дата окончания периода в UTC
     * @return список сообщений из чата
     */
    public List<TgMessageDto> collectAllMessagesFromPublicChat(Long chatId,
                                                               @Nullable Integer limit,
                                                               @Nullable LocalDateTime dateFrom,
                                                               @Nullable LocalDateTime dateTo) {
        List<TgMessageDto> messageDtos = new ArrayList<>();
        long fromMessageId = 0L;
        if (dateFrom == null) {
            dateFrom = LocalDateTime.of(
                LocalDate.now(ZoneOffset.UTC).minusDays(tgChatLoaderProperty.defaultDaysBeforeLimit()),
                LocalTime.MIN);
        }

        int limitMessagesToLoad = limit == null ? tgChatLoaderProperty.defaultMessagesLimit() : limit;

        while (messageDtos.size() < limitMessagesToLoad) {
            TdApi.Messages messages = collectPublicChatMessages(chatId, fromMessageId);

            if (messages.totalCount == 0) {
                log.info("Извлечено сообщений: {}", messageDtos.size());

                return messageDtos;
            }

            log.info("Загружено сообщений в пачке: {}", messages.messages.length);

            for (TdApi.Message message : messages.messages) {
                TdApi.MessageContent content = message.content;

                if (content instanceof TdApi.MessageText messageText) {
                    LocalDateTime messageDateTimeUtc = LocalDateTime.ofEpochSecond(message.date, 0, ZoneOffset.UTC);
                    LocalDateTime messageDateTime = messageDateTimeUtc
                        .plusHours(3); // Московское время

                    if (dateTo != null && messageDateTimeUtc.isAfter(dateTo)) {
                        continue;
                    }

                    if (messageDateTimeUtc.isBefore(dateFrom)) {
                        log.info("Извлечено сообщений: {}", messageDtos.size());

                        return messageDtos;
                    }

                    messageDtos.add(TgMessageDto.builder()
                        .dateTime(messageDateTime)
                        .text(messageText.text.text)
                        .build());
                }

                if (messages.messages.length == 0) {
                    break;
                }

                fromMessageId = messages.messages[messages.messages.length - 1].id;
            }
        }

        log.info("Извлечено сообщений: {}", messageDtos.size());

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
