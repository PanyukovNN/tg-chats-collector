package ru.panyukovnn.tgchatscollector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.panyukovnn.tgchatscollector.model.SubscribedChannel;

import java.util.UUID;

@RepositoryRestResource
public interface SubscribedChannelsRepository extends JpaRepository<SubscribedChannel, UUID> {
}
