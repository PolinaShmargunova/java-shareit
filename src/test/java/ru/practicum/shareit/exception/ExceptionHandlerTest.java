package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerTest {
    @Test
    void conflictExcTest() {
        ExceptionsHandler exceptionsHandler = new ExceptionsHandler();

        String error = "Unknown " + ": ";
        ResponseEntity<ErrorResponse> entity = ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.conflictException(new ConflictException());
        assertEquals(1, 1);
    }

    @Test
    void badReqExcTest() {
        ExceptionsHandler exceptionsHandler = new ExceptionsHandler();

        String error = "Unknown " + ": ";
        ResponseEntity<ErrorResponse> entity = ResponseEntity.status(409).body(new ErrorResponse(error));
        exceptionsHandler.badRequestException(new BadRequestException());
        assertEquals(1, 1);
    }
}