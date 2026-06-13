package com.jame.dev.gymApp.features.subscription.domain.exception;

public class StripeSessionCreationException extends RuntimeException {
    public StripeSessionCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
