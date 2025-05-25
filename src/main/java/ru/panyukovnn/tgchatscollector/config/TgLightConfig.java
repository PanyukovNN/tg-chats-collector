package ru.panyukovnn.tgchatscollector.config;

import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.*;
import it.tdlight.jni.TdApi;
import it.tdlight.util.UnsupportedNativeLibraryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.panyukovnn.tgchatscollector.property.TgCollectorProperty;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class TgLightConfig {

    @Bean(destroyMethod = "close")
    public SimpleTelegramClientFactory simpleTelegramClientFactory() throws UnsupportedNativeLibraryException {
        // Initialize TDLight native libraries
        Init.init();

        // Set the log level
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());

        return new SimpleTelegramClientFactory();
    }

    @Bean(destroyMethod = "close")
    public SimpleTelegramClient tgClient(TgCollectorProperty tgCollectorProperty, SimpleTelegramClientFactory simpleTelegramClientFactory) {
        APIToken apiToken = new APIToken(tgCollectorProperty.apiId(), tgCollectorProperty.apiHash());

        Path sessionPath = Paths.get("tdlight-session");

        TDLibSettings settings = TDLibSettings.create(apiToken);
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

        SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.user(tgCollectorProperty.phone());
        SimpleTelegramClient client = simpleTelegramClientFactory.builder(settings)
            .build(authenticationData);

        client.addUpdateHandler(TdApi.UpdateAuthorizationState.class, this::onUpdateAuthorizationState);

        return client;
    }

    private void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        TdApi.AuthorizationState authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            log.info("Logged in");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            log.info("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            log.info("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            log.info("Logging out...");
        }
    }

    private void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
        TdApi.MessageContent messageContent = update.message.content;

        String text;
        if (messageContent instanceof TdApi.MessageText messageText) {
            text = messageText.text.text;
        } else {
            text = String.format("(%s)", messageContent.getClass().getSimpleName());
        }

        if (text.length() > 100) {
            text = text.substring(0, 100) + "...";
        }

        long chatId = update.message.chatId;

        log.info("Received new message from chat {}: {}", chatId, text);
    }
}
