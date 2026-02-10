package com.schooltrack.controller;

import com.schooltrack.dto.StudentResponse;
import com.schooltrack.repository.StudentRepository;
import com.schooltrack.repository.UserRepository;
import com.schooltrack.service.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getStudents(Authentication authentication) {
        var user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return ResponseEntity.ok(
                studentRepository.findByParentId(user.getId()).stream()
                        .map(dtoMapper::toStudentResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
        var student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        return ResponseEntity.ok(dtoMapper.toStudentResponse(student));
    }
}
