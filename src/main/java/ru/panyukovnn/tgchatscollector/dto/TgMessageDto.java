package ru.panyukovnn.tgchatscollector.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TgMessageDto {

    private LocalDateTime dateTime;
    private String text;
}
