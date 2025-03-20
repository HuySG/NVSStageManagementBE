package com.nvsstagemanagement.nvs_stage_management.exception;

import lombok.Data;

@Data
public class ApiErrorResponse {
    private String errorCode;
    private String errorMessage;

    public ApiErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
