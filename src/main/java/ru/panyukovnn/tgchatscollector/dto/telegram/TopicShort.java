package ru.panyukovnn.tgchatscollector.dto.telegram;

public record TopicShort(
    Long topicId,
    String title,
    Long lastMessageId) {
}
