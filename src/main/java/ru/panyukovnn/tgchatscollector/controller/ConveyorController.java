package ru.panyukovnn.tgchatscollector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panyukovnn.tgchatscollector.dto.ProposeToConveyorRequest;
import ru.panyukovnn.tgchatscollector.dto.common.CommonRequest;
import ru.panyukovnn.tgchatscollector.dto.common.CommonResponse;
import ru.panyukovnn.tgchatscollector.service.handler.ConveyorHandler;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConveyorController {

    private final ConveyorHandler conveyorHandler;

    @PostMapping("/proposeToConveyor")
    public CommonResponse<Void> postProposeToConveyor(@RequestBody @Valid CommonRequest<ProposeToConveyorRequest> request) {
        conveyorHandler.handleConveyor(request.getBody());

        return CommonResponse.<Void>builder()
            .build();
    }
}
