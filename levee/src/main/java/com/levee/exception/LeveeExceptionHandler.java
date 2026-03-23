package com.levee.exception;

import com.levee.contract.LeveeErrorResponse;
import com.levee.contract.LeveeUsageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class LeveeExceptionHandler{

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<LeveeUsageResponse> handleRateLimitExceededExcpetion(
            RateLimitExceededException exception
    ) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(exception.getResponse());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<LeveeErrorResponse> handleIllegalArguementException(
            IllegalArgumentException exception
    ) {
        LeveeErrorResponse errorResponse = new LeveeErrorResponse(
                "Bad request",
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<LeveeErrorResponse> handleNoSuchElementException(
            NoSuchElementException exception
    ) {
        LeveeErrorResponse errorResponse = new LeveeErrorResponse(
                "Bad request",
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
