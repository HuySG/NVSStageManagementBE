package com.nvsstagemanagement.nvs_stage_management.dto.exception.room;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class RoomNotEnabledException extends RuntimeException {

    private String message;

    private HttpStatusCode httpStatus = HttpStatus.BAD_REQUEST;

    public RoomNotEnabledException(String message) {
        this.message = message;
    }

}
