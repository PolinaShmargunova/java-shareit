package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> constraintException(final MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException ");
        String error = "Unknown " + e.getName() + ": " + e.getValue();
        return ResponseEntity.status(400).body(new ErrorResponse(error));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> notFoundException(final NotFoundException e) {
        log.error("NotFoundException ");
        String error = "Not found";
        return ResponseEntity.status(404).body(new ErrorResponse(error));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> badRequestException(final NullPointerException e) {
        log.error("badRequestException ");
        String error = "Bad request";
        return ResponseEntity.status(400).body(new ErrorResponse(error));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> conflictException(final ConflictException e) {
        log.error("conflictException ");
        String error = "Conflict";
        return ResponseEntity.status(409).body(new ErrorResponse(error));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> badRequestException(final BadRequestException e) {
        log.error("badRequestException ");
        String error = "Bad request";
        return ResponseEntity.status(400).body(new ErrorResponse(error));
    }
}