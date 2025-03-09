package com.nvsstagemanagement.nvs_stage_management.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



public class GlobalExceptionHandler {
    @ExceptionHandler(NotEnoughAssetException.class)
    public ResponseEntity<String> handleNotEnoughAssetException(NotEnoughAssetException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
