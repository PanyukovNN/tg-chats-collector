package ru.panyukovnn.tgchatscollector.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscribed_channels")
public class SubscribedChannel extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long chatId;
    private String chatName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SubscribedChannel subscribedChannel = (SubscribedChannel) o;
        return Objects.equals(id, subscribedChannel.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
