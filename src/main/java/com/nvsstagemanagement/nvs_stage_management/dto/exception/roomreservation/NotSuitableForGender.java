package com.nvsstagemanagement.nvs_stage_management.dto.exception.roomreservation;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class NotSuitableForGender extends RuntimeException {

    private String message;

    private HttpStatusCode httpStatus = HttpStatus.BAD_REQUEST;

    public NotSuitableForGender(String message) {
        this.message = message;
    }

}
