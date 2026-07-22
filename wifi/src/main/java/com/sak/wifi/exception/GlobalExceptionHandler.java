package com.sak.wifi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse>
    handleNotFound( ResourceNotFoundException ex){
            ErrorResponse error =new ErrorResponse(
                    LocalDateTime.now(),
                    ex.getMessage(),
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<>(
                    error,
                    HttpStatus.NOT_FOUND
            );
    }


    @ExceptionHandler(
            ResourceAlreadyExistsException.class
    )
    public ResponseEntity<ErrorResponse>
    handleAlreadyExists(
            ResourceAlreadyExistsException ex
    ) {

        ErrorResponse error =
                new ErrorResponse(
                        LocalDateTime.now(),
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value()
                );

        return new ResponseEntity<>(
                error,
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    handleValidation(MethodArgumentNotValidException ex){

        String message= ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ErrorResponse error= new ErrorResponse(
                LocalDateTime.now(),
                message,
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.BAD_REQUEST
        );

    }
}
