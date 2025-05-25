package ru.panyukovnn.tgchatscollector.dto.telegram;

public record ChatShort(
    Long chatId,
    String chatPublicName,
    String title) {
}
