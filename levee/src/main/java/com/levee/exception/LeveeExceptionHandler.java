package com.levee.exception;

import com.levee.contract.LeveeErrorResponse;
import com.levee.contract.LeveeUsageResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.MismatchedInputException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class LeveeExceptionHandler{
    private static final Logger logger = LoggerFactory.getLogger(LeveeExceptionHandler.class);

    @ExceptionHandler({MismatchedInputException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<LeveeErrorResponse> handleMismatchedInputExcpetion(
            Exception exception
    ) {
        LeveeErrorResponse errorResponse = new LeveeErrorResponse(
                "Bad request",
                "Invalid request. Please check request bpdy"
        );
        logger.error("ERROR :: Mismatched input exception occurred :: {}",exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<LeveeUsageResponse> handleRateLimitExceededExcpetion(
            RateLimitExceededException exception
    ) {
        logger.error("ERROR :: Rate limit exception occurred :: {}",exception.getMessage());
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
        logger.error("ERROR :: Illegal arguement exception occurred :: {}",exception.getMessage());
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
        logger.error("ERROR :: No such element exception occurred :: {}",exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
