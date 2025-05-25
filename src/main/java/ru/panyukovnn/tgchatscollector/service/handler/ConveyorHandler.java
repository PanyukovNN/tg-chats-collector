package ru.panyukovnn.tgchatscollector.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.tgchatscollector.dto.ConsumeContentRequest;
import ru.panyukovnn.tgchatscollector.dto.ProposeToConveyorRequest;
import ru.panyukovnn.tgchatscollector.dto.TgMessageDto;
import ru.panyukovnn.tgchatscollector.dto.telegram.ChatShort;
import ru.panyukovnn.tgchatscollector.dto.telegram.TopicShort;
import ru.panyukovnn.tgchatscollector.exception.TgChatsCollectorException;
import ru.panyukovnn.tgchatscollector.service.TgClientService;
import ru.panyukovnn.tgchatscollector.service.client.ConveyorClientService;
import ru.panyukovnn.tgchatscollector.util.JsonUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConveyorHandler {

    private final JsonUtil jsonUtil;
    private final TgClientService tgClientService;
    private final ConveyorClientService conveyorClientService;

    public void handleConveyor(ProposeToConveyorRequest proposeRequest) {
//        ChatShort chat = tgClientService.findChat(proposeRequest.getChatId(), proposeRequest.getPublicChatName(), proposeRequest.getPrivateChatNamePart())
//            .orElseThrow(() -> new TgChatsCollectorException("31ff", "Не удалось найти чат по запросу: " + proposeRequest));
//
//        TopicShort topic = Optional.ofNullable(proposeRequest.getTopicName())
//            .map(topicName -> tgClientService.findTopicByName(chat.chatId(), topicName))
//            .orElse(null);
//
//        List<TgMessageDto> messageDtos = tgClientService.collectAllMessagesFromPublicChat(
//            chat.chatId(),
//            topic,
//            proposeRequest.getLimit(),
//            LocalDateTime.of(proposeRequest.getDayFrom(), LocalTime.MIN),
//            LocalDateTime.of(proposeRequest.getDayTo(), LocalTime.MAX));
//
//        if (messageDtos.isEmpty()) {
//            log.warn("Не найдены сообщения по указанным параметрам");
//
//            return;
//        }
//
//        String jsonMessages = jsonUtil.toJson(messageDtos);
//
//        ConsumeContentRequest contentRequest = ConsumeContentRequest.builder()
//            .link(chat.title())
//            .contentType("tg_message_batch")
//            .source("tg")
//            .title(chat.title() + " " + proposeRequest.getDayFrom() + "-" + proposeRequest.getDayTo())
//            .meta(null)
//            .publicationDate(LocalDateTime.of(proposeRequest.getDayFrom(), LocalTime.MIN))
//            .content(jsonMessages)
//            .conveyorType("just_retelling")
//            .conveyorTag("tg_message_batch")
//            .build();
//
//        conveyorClientService.sendContentToConveyor(contentRequest);
    }


}
