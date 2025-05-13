package ru.panyukovnn.tgchatscollector.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.panyukovnn.tgchatscollector.repository.TgMessagesRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TgMessagesService {

    private final TgMessagesRepository tgMessagesRepository;


}
