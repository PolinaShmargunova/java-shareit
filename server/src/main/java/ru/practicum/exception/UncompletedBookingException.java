package ru.practicum.exception;

public class UncompletedBookingException extends RuntimeException {

    public UncompletedBookingException(String message) {
        super(message);
    }

}
