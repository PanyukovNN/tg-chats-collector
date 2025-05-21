package ru.panyukovnn.tgchatscollector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgMessageSenderService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${tg-collector.telegram.bot.token}")
    private String botToken;

    public void sendMessages(List<String> messages, String chatId) {
        if (messages.isEmpty()) {
            log.info("Нет сообщений для отправки");
            return;
        }

        String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        for (String message : messages) {
            try {
                SendMessageRequest request = new SendMessageRequest(chatId, message, 104);
                String jsonBody = objectMapper.writeValueAsString(request);
                
                log.info("Отправка запроса в Telegram. URL: {}, Body: {}", apiUrl, jsonBody);
                
                var response = restClient.post()
                    .uri(apiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonBody)
                    .retrieve()
                    .toEntity(String.class);
                
                log.info("Получен ответ от Telegram: {}", response.getBody());
                
            } catch (Exception e) {
                log.error("Ошибка при отправке сообщения в TG: {}", e.getMessage(), e);
            }
        }
    }

    private record SendMessageRequest(
        String chat_id,
        String text,
        Integer message_thread_id
    ) {}
}