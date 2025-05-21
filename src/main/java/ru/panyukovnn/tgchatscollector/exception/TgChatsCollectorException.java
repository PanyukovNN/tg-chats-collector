package ru.panyukovnn.tgchatscollector.exception;

import lombok.Getter;

@Getter
public class TgChatsCollectorException extends RuntimeException {

    private final String id;

    public TgChatsCollectorException(String id, String message) {
        super(message);
        this.id = id;
    }

    public TgChatsCollectorException(String id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }
}
