package ru.panyukovnn.tgchatscollector.service.handler;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.panyukovnn.tgchatscollector.dto.TgMessageDto;
import ru.panyukovnn.tgchatscollector.dto.chathistory.ChatHistoryResponse;
import ru.panyukovnn.tgchatscollector.dto.chathistory.MessageDto;
import ru.panyukovnn.tgchatscollector.dto.chathistory.MessagesBatch;
import ru.panyukovnn.tgchatscollector.dto.telegram.ChatShort;
import ru.panyukovnn.tgchatscollector.dto.telegram.TopicShort;
import ru.panyukovnn.tgchatscollector.exception.TgChatsCollectorException;
import ru.panyukovnn.tgchatscollector.service.TgClientService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgCollectorHandler {

    private static final Integer MAX_WORDS_PER_BATCH = 12000;

    private final TgClientService tgClientService;

    public ChatHistoryResponse handleChatHistory(String publicChatName,
                                                 String privateChatNamePart,
                                                 String topicName,
                                                 @Nullable Integer limit,
                                                 @Nullable LocalDateTime dateFrom,
                                                 @Nullable LocalDateTime dateTo) {
        if (!StringUtils.hasText(publicChatName)
            && !StringUtils.hasText(privateChatNamePart)
            && !StringUtils.hasText(topicName)) {
            throw new IllegalArgumentException("publicChatName, privateChatNamePart, topicName не могут быть одновременно пустыми");
        }

        ChatShort chat = tgClientService.findChat(null, publicChatName, privateChatNamePart)
            .orElseThrow(() -> new TgChatsCollectorException("31ff", "Не удалось найти чат"));
        TopicShort topic = Optional.ofNullable(topicName)
            .map(tn -> tgClientService.findTopicByName(chat.chatId(), tn))
            .orElse(null);

        List<TgMessageDto> messageDtos = tgClientService.collectAllMessagesFromPublicChat(chat.chatId(), topic, limit, dateFrom, dateTo);

        LocalDateTime firstMessageDateTime = !messageDtos.isEmpty() ? messageDtos.get(0).getDateTime() : null;
        LocalDateTime lastMessageDateTime = !messageDtos.isEmpty() ? messageDtos.get(messageDtos.size() - 1).getDateTime() : null;

        List<MessagesBatch> messageBatches = createMessageBatches(messageDtos);

        int totalCount = messageBatches.stream()
            .map(MessagesBatch::getCount)
            .reduce(0, Integer::sum);

        return ChatHistoryResponse.builder()
            .chatId(chat.chatId())
            .chatPublicName(chat.chatPublicName())
            .topicName(topicName)
            .firstMessageDateTime(firstMessageDateTime)
            .lastMessageDateTime(lastMessageDateTime)
            .totalCount(totalCount)
            .messages(messageBatches)
            .build();
    }

    private List<MessagesBatch> createMessageBatches(List<TgMessageDto> messageDtos) {
        List<MessagesBatch> batches = new ArrayList<>();
        List<MessageDto> currentBatch = new ArrayList<>();
        int currentWordCount = 0;

        for (TgMessageDto message : messageDtos) {
            String text = message.getText();
            int messageWordCount = text.split("\\s+").length;

            String replyToText = message.getReplyToText();
            if (replyToText != null) {
                messageWordCount += replyToText.length();
            }

            if (currentWordCount + messageWordCount > MAX_WORDS_PER_BATCH && !currentBatch.isEmpty()) {
                batches.add(MessagesBatch.builder()
                    .count(currentBatch.size())
                    .messages(new ArrayList<>(currentBatch))
                    .build());
                currentBatch.clear();
                currentWordCount = 0;
            }

            currentBatch.add(MessageDto.builder()
                .senderId(message.getSenderId())
                .replyToText(message.getReplyToText())
                .id(message.getMessageId())
                .text(text)
                .build());
            currentWordCount += messageWordCount;
        }

        if (!currentBatch.isEmpty()) {
            batches.add(MessagesBatch.builder()
                .count(currentBatch.size())
                .messages(currentBatch)
                .build());
        }

        return batches;
    }

}
