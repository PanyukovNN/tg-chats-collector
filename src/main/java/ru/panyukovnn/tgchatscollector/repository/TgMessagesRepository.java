package ru.panyukovnn.tgchatscollector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panyukovnn.tgchatscollector.model.TgMessage;

import java.util.UUID;

public interface TgMessagesRepository extends JpaRepository<TgMessage, UUID> {
}
