package ru.panyukovnn.tgchatscollector.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.panyukovnn.tgchatscollector.dto.TgMessageDto;
import ru.panyukovnn.tgchatscollector.service.TgClientService;
import ru.panyukovnn.tgchatscollector.service.TgMessageSenderService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlexeyJob {

    @Value("${tg-collector.telegram.bot.sk-reading-chat-id}")
    private Long skReadingChatId;
    @Value("${tg-collector.telegram.bot.sk-publishing-chat-id}")
    private String skPublishingChatId;

    private final TgClientService tgClientService;
    private final TgMessageSenderService tgMessageSenderService;

    @Async("collectMessagesForAlexeyJob")
    @Scheduled(cron = "0 0 * * * *")
    public void collectMessagesForAlexey() {
        log.info("Запуск задачи сбора сообщений из чата СК");
        
        try {
            LocalDateTime dateFrom = LocalDateTime.of(
                LocalDate.now(ZoneOffset.UTC),
                LocalTime.of(LocalTime.now(ZoneOffset.UTC).getHour(), 0, 0)
            );

            List<String> messagesAboutKomiRepublic = tgClientService.collectAllMessagesFromPublicChat(skReadingChatId, null, null, dateFrom, null).stream()
                .map(TgMessageDto::getText)
                .filter(text -> text.contains("Коми"))
                .toList();
            
            log.info("Собрано {} сообщений про Республику Коми из чата СК", messagesAboutKomiRepublic.size());
                
            tgMessageSenderService.sendMessages(messagesAboutKomiRepublic, skPublishingChatId);
        } catch (Exception e) {
            log.error("Ошибка при сборе сообщений из чата СК: {}", e.getMessage(), e);
        }
    }
} 