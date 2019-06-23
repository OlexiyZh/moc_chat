package com.mochallenge.chat.exception;

/**
 * Thrown when an entity is not found in the database.
 */
public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String message) {
        super(message);
    }
}
