package ru.panyukovnn.tgchatscollector.service.handler;

import it.tdlight.jni.TdApi;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.tgchatscollector.dto.ChatHistoryResponse;
import ru.panyukovnn.tgchatscollector.dto.TgMessageDto;
import ru.panyukovnn.tgchatscollector.service.TgClientService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgCollectorHandler {

    private final TgClientService tgClientService;

    public ChatHistoryResponse handleChatHistory(Long chatId, String chatName, @Nullable Integer limit, @Nullable LocalDateTime dateFrom) {
        TdApi.Chat chat = tgClientService.findChat(chatId, chatName);

        List<TgMessageDto> messageDtos = tgClientService.collectAllMessagesFromPublicChat(chat.id, limit, dateFrom, null);

        LocalDateTime firstMessageDateTime = !messageDtos.isEmpty() ? messageDtos.get(0).getDateTime() : null;
        LocalDateTime lastMessageDateTime = !messageDtos.isEmpty() ? messageDtos.get(messageDtos.size() - 1).getDateTime() : null;

        List<String> textMessages = messageDtos.stream()
            .map(TgMessageDto::getText)
            .toList();

        return ChatHistoryResponse.builder()
            .chatId(chat.id)
            .chatName(chat.title)
            .firstMessageDateTime(firstMessageDateTime)
            .lastMessageDateTime(lastMessageDateTime)
            .messages(textMessages)
            .build();
    }

}
