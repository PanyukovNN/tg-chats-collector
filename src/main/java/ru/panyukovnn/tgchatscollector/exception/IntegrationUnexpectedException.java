package ru.panyukovnn.tgchatscollector.exception;

import lombok.Getter;

@Getter
public class IntegrationUnexpectedException extends RuntimeException {

    private final String id;

    public IntegrationUnexpectedException(String id, String message) {
        super(message);
        this.id = id;
    }

    public IntegrationUnexpectedException(String id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
    }
}
