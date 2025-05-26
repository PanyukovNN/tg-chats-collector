package ru.panyukovnn.tgchatscollector.dto.telegram;

public record TopicShort(
    Boolean isGeneral,
    Long topicId,
    String title,
    Long lastMessageId) {
}
