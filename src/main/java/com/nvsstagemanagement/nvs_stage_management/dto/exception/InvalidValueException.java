package com.nvsstagemanagement.nvs_stage_management.dto.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class InvalidValueException extends RuntimeException {

    private String message;

    private HttpStatusCode httpStatus = HttpStatus.BAD_REQUEST;

    public InvalidValueException(String message) {
        this.message = message;
    }

}
