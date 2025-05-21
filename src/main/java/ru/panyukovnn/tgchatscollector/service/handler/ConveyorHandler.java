package ru.panyukovnn.tgchatscollector.service.handler;

import it.tdlight.jni.TdApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.tgchatscollector.dto.ConsumeContentRequest;
import ru.panyukovnn.tgchatscollector.dto.ProposeToConveyorRequest;
import ru.panyukovnn.tgchatscollector.dto.TgMessageDto;
import ru.panyukovnn.tgchatscollector.service.TgClientService;
import ru.panyukovnn.tgchatscollector.service.client.ConveyorClientService;
import ru.panyukovnn.tgchatscollector.util.JsonUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConveyorHandler {

    private final JsonUtil jsonUtil;
    private final TgClientService tgClientService;
    private final ConveyorClientService conveyorClientService;

    public void handleConveyor(ProposeToConveyorRequest proposeRequest) {
        TdApi.Chat chat = tgClientService.findChat(proposeRequest.getChatId(), proposeRequest.getChatName());

        // TODO topicName пока не учитывается

        List<TgMessageDto> messageDtos = tgClientService.collectAllMessagesFromPublicChat(
            chat.id,
            proposeRequest.getLimit(),
            LocalDateTime.of(proposeRequest.getDayFrom(), LocalTime.MIN),
            LocalDateTime.of(proposeRequest.getDayTo(), LocalTime.MAX));

        if (messageDtos.isEmpty()) {
            log.warn("Не найдены сообщения по указанным параметрам");

            return;
        }

        String jsonMessages = jsonUtil.toJson(messageDtos);

        ConsumeContentRequest contentRequest = ConsumeContentRequest.builder()
            .link(chat.title)
            .contentType("tg_message_batch")
            .source("tg")
            .title(chat.title + " " + proposeRequest.getDayFrom() + "-" + proposeRequest.getDayTo())
            .meta(null)
            .publicationDate(LocalDateTime.of(proposeRequest.getDayFrom(), LocalTime.MIN))
            .content(jsonMessages)
            .conveyorType("just_retelling")
            .conveyorTag("tg_message_batch")
            .build();

        conveyorClientService.sendContentToConveyor(contentRequest);
    }
}
