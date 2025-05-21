package ru.panyukovnn.tgchatscollector.service.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.panyukovnn.tgchatscollector.client.ConveyorFeignClient;
import ru.panyukovnn.tgchatscollector.dto.ConsumeContentRequest;
import ru.panyukovnn.tgchatscollector.dto.common.CommonRequest;
import ru.panyukovnn.tgchatscollector.dto.common.CommonResponse;
import ru.panyukovnn.tgchatscollector.exception.IntegrationBusinessException;
import ru.panyukovnn.tgchatscollector.exception.IntegrationUnexpectedException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConveyorClientService {

    private final ConveyorFeignClient conveyorFeignClient;

    public void sendContentToConveyor(ConsumeContentRequest contentRequest) {
        try {
            CommonResponse<Void> commonResponse = conveyorFeignClient.postConsume(CommonRequest.<ConsumeContentRequest>builder()
                .body(contentRequest)
                .build());

            if (StringUtils.hasText(commonResponse.getErrorMessage())) {
                throw new IntegrationBusinessException("1f28", "Получено сообщение об ошибке от сервиса conveyor: " + commonResponse.getErrorMessage());
            }

            log.info("Контент успешно отправлен в conveyor");
        } catch (FeignException feignException) {
            throw new IntegrationBusinessException(
                "46e4",
                "Ошибка при обращении к сервису conveyor. Статус ответа: %s. Тело ответа: %s. Сообщение об ошибке: %s"
                    .formatted(feignException.status(), feignException.responseBody(), feignException.getMessage()),
                feignException);
        } catch (Exception e) {
            throw new IntegrationUnexpectedException("b2ea", "Непредвиденная ошибка при обращении к сервису content: " + e.getMessage(), e);
        }
    }
}
