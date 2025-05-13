package ru.panyukovnn.tgchatscollector.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tg-collector.telegram.client")
public record TgCollectorProperty (
    Integer apiId,
    String apiHash,
    String phone
) {
}
