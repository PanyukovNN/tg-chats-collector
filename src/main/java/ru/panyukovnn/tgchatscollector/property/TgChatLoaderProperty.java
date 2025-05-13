package ru.panyukovnn.tgchatscollector.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tg-collector.chat-loader")
public record TgChatLoaderProperty(
    Integer defaultMessagesLimit,
    Integer defaultDaysLimit
) {
}
