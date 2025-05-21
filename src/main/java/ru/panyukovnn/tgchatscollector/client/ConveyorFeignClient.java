package ru.panyukovnn.tgchatscollector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.panyukovnn.tgchatscollector.dto.ConsumeContentRequest;
import ru.panyukovnn.tgchatscollector.dto.common.CommonRequest;
import ru.panyukovnn.tgchatscollector.dto.common.CommonResponse;

@FeignClient(url = "${tg-collector.integration.conveyor.host}/conveyor/api/v1/content", name = "conveyor")
public interface ConveyorFeignClient {

    @PostMapping("/consume")
    CommonResponse<Void> postConsume(CommonRequest<ConsumeContentRequest> request);

}
