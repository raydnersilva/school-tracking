package com.schooltrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CaptchaValidationRequest {

    @NotBlank
    private String captchaId;

    @NotBlank
    private String answer;
}
