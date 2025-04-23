package com.nvsstagemanagement.nvs_stage_management.enums;

public enum ProjectStatus {
    /**
     * Project vừa được tạo, chưa có milestone nào.
     */
    NEW,

    /**
     * Project đang có milestone hoặc task được triển khai.
     */
    IN_PROGRESS,

    /**
     * Project đã hoàn tất
     */
    COMPLETED,

    /**
     * Project bị dừng giữa chừng hoặc huỷ bỏ.
     */
    CANCELLED
}
