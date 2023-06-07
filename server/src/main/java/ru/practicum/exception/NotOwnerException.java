package ru.practicum.exception;

public class NotOwnerException extends RuntimeException {

    public NotOwnerException() {
    }

    public NotOwnerException(String message) {
        super(message);
    }

}

