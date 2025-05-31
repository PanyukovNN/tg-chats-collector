package ru.panyukovnn.tgchatscollector.dto.chathistory;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChatHistoryResponse {

    private Long chatId;
    private String chatTitle;
    private String chatPublicName;
    private String topicName;
    private Long topicId;
    private LocalDateTime firstMessageDateTime;
    private LocalDateTime lastMessageDateTime;
    private Integer totalCount;
    private List<MessagesBatch> messageBatches;
}
