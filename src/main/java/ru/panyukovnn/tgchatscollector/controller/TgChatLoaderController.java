package ru.panyukovnn.tgchatscollector.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.tgchatscollector.dto.ChatHistoryResponse;
import ru.panyukovnn.tgchatscollector.service.handler.TgCollectorHandler;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TgChatLoaderController {

    private final TgCollectorHandler tgCollectorHandler;

    @GetMapping("/getChatHistory")
    public ChatHistoryResponse getChatHistory(@RequestParam(required = false) Long chatId,
                                              @RequestParam(required = false) Integer limit,
                                              @RequestParam(required = false) String chatName,
                                              @Schema(description = "Дата до которой будут извлекаться сообщения, в UTC")
                                              @RequestParam(required = false) LocalDateTime dateFrom) {
        return tgCollectorHandler.handleChatHistory(chatId, chatName, limit, dateFrom);
    }
}
