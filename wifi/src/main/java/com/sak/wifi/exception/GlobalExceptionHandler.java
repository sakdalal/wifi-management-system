package com.sak.wifi.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceAlreadyExistsException.class
    )
    public ResponseEntity<String>
    handle(ResourceAlreadyExistsException ex){
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
