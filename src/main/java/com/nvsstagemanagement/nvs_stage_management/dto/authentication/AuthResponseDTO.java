package com.nvsstagemanagement.nvs_stage_management.dto.authentication;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthResponseDTO {
    @NonNull
    private String accessToken;
    private String typeToken = new String("Bearer ");
}
