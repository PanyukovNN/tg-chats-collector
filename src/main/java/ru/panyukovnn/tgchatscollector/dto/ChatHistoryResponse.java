package ru.panyukovnn.tgchatscollector.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChatHistoryResponse {

    private Long chatId;
    private String chatName;
    private LocalDateTime firstMessageDateTime;
    private LocalDateTime lastMessageDateTime;
    private List<String> messages;
}
