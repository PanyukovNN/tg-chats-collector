package ru.panyukovnn.tgchatscollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.panyukovnn.tgchatscollector.property.TgChatLoaderProperty;
import ru.panyukovnn.tgchatscollector.property.TgCollectorProperty;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableConfigurationProperties({TgCollectorProperty.class, TgChatLoaderProperty.class})
public class TgChatsCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgChatsCollectorApplication.class, args);
    }
}
