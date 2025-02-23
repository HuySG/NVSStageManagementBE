package com.nvsstagemanagement.nvs_stage_management.dto.exception.sesmester;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class SesmesterDateValidationException extends RuntimeException {

    private String message;

    private HttpStatusCode httpStatus = HttpStatus.BAD_REQUEST;

    public SesmesterDateValidationException(String message) {
        this.message = message;
    }

}
