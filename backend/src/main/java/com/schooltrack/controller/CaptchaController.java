package com.schooltrack.controller;

import com.schooltrack.dto.CaptchaResponse;
import com.schooltrack.dto.CaptchaValidationRequest;
import com.schooltrack.service.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    public ResponseEntity<CaptchaResponse> generateCaptcha() {
        return ResponseEntity.ok(captchaService.generateCaptcha());
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateCaptcha(@Valid @RequestBody CaptchaValidationRequest request) {
        boolean valid = captchaService.validateCaptcha(request.getCaptchaId(), request.getAnswer());
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}
