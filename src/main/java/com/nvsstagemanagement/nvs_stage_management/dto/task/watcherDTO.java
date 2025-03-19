package com.nvsstagemanagement.nvs_stage_management.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class watcherDTO {
    private String userID;
    private String fullName;
    private LocalDate dayOfBirth;
    private String email;
    private String pictureProfile;
}
