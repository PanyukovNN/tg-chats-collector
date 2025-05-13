package ru.panyukovnn.tgchatscollector.service.handler;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

    public ChatHistoryResponse handleChatHistory(Long chatId, String chatName, @Nullable Integer limit, @Nullable Integer daysLimit) {
        if (chatId == null && !StringUtils.hasText(chatName)) {
            throw new IllegalArgumentException("Идентификатор чата и имя чата не могут быть одновременно пустыми");
        }

        Long definedChatId = definedChatId(chatId, chatName);
        String definedChatName = definedChatName(chatId, chatName);

        List<TgMessageDto> messageDtos = tgClientService.collectAllMessagesFromPublicChat(definedChatId, limit, daysLimit);

        LocalDateTime firstMessageDateTime = !messageDtos.isEmpty() ? messageDtos.get(0).getDateTime() : null;
        LocalDateTime lastMessageDateTime = !messageDtos.isEmpty() ? messageDtos.get(messageDtos.size() - 1).getDateTime() : null;

        List<String> textMessages = messageDtos.stream()
            .map(TgMessageDto::getText)
            .toList();

        return ChatHistoryResponse.builder()
            .chatId(definedChatId)
            .chatName(definedChatName)
            .firstMessageDateTime(firstMessageDateTime)
            .lastMessageDateTime(lastMessageDateTime)
            .messages(textMessages)
            .build();
    }

    private String definedChatName(Long chatId, String chatName) {
        return chatName != null
            ? chatName
            : tgClientService.getChatNameById(chatId);
    }

    private Long definedChatId(Long chatId, String chatName) {
        return chatId != null
            ? chatId
            : tgClientService.findChatIdByPublicChatName(chatName);
    }

}
