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
}
