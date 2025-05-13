package ru.panyukovnn.tgchatscollector.config;

import it.tdlight.client.SimpleTelegramClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configuration
@RequiredArgsConstructor
public class TgLightAuthorizeConfig {

    private final SimpleTelegramClient tgClient;

    @EventListener(ApplicationReadyEvent.class)
    public void authorizeTgClient() throws ExecutionException, InterruptedException, TimeoutException {
        tgClient.getMeAsync()
            .get(10, TimeUnit.MINUTES);
    }
}
