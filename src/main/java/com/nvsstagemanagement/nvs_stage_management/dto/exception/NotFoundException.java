package com.nvsstagemanagement.nvs_stage_management.dto.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class NotFoundException extends RuntimeException {

    private String message;

    private HttpStatusCode httpStatus = HttpStatus.NOT_FOUND ;

    public NotFoundException(String message) {
        this.message = message;
    }

}
