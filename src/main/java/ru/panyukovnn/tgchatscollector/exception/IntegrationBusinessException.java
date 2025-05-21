package ru.panyukovnn.tgchatscollector.exception;

import lombok.Getter;

@Getter
public class IntegrationBusinessException extends RuntimeException {

    private final String id;

    public IntegrationBusinessException(String id, String message) {
        super(message);
        this.id = id;
    }

    public IntegrationBusinessException(String id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }
}
