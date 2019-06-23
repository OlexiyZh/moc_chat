package com.mochallenge.chat.exception;

/**
 * Thrown when validating
 */
public class ChatValidationException extends RuntimeException {

    public ChatValidationException(String message) {
        super(message);
    }
}
