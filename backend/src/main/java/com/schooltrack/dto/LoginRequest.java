package com.schooltrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @NotBlank(message = "Captcha ID é obrigatório")
    private String captchaId;

    @NotBlank(message = "Resposta do captcha é obrigatória")
    private String captchaAnswer;
}
