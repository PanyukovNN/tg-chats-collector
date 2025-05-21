package ru.panyukovnn.tgchatscollector.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposeToConveyorRequest {

    @Schema(description = "Идентификатор чата")
    private Long chatId;

    @Schema(description = "Имя чата")
    private String chatName;

    @Schema(description = "Имя топика")
    private String topicName;

    @Schema(description = "Лимит сообщений")
    private Integer limit;

    @NotNull
    @Schema(description = "День начала периода")
    private LocalDate dayFrom;

    @NotNull
    @Schema(description = "День окончания периода (включительно)")
    private LocalDate dayTo;
}
