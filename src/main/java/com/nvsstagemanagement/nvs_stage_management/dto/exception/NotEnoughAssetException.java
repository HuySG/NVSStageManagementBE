package com.nvsstagemanagement.nvs_stage_management.dto.exception;

import lombok.Getter;

@Getter
public class NotEnoughAssetException extends RuntimeException  {
    public NotEnoughAssetException(String message) {
        super(message);
    }
}
