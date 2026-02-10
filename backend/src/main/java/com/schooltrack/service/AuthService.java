package com.schooltrack.service;

import com.schooltrack.dto.*;
import com.schooltrack.model.Role;
import com.schooltrack.model.User;
import com.schooltrack.repository.UserRepository;
import com.schooltrack.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CaptchaService captchaService;

    public AuthResponse login(LoginRequest request) {
        if (!captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptchaAnswer())) {
            throw new RuntimeException("CAPTCHA inválido");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha incorreta");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name().toLowerCase());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name().toLowerCase())
                .name(user.getName())
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        String username = request.getEmail().split("@")[0];
        if (userRepository.existsByUsername(username)) {
            username = username + System.currentTimeMillis();
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PARENT)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name().toLowerCase());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name().toLowerCase())
                .name(user.getName())
                .build();
    }

    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        // TODO: Implementar envio de email real
        return new MessageResponse("Link de recuperação enviado para " + request.getEmail());
    }
}
