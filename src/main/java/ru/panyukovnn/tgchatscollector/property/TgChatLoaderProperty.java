package ru.panyukovnn.tgchatscollector.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tg-collector.chat-loader")
public record TgChatLoaderProperty(

    /**
     * Количество дней за которые будут извлечены сообщения из чата
     */
    Integer defaultMessagesLimit,

    /**
     * Количество дней за которые будут извлечены сообщения из чата
     */
    Integer defaultDaysBeforeLimit
) {
}
