package dev.trainerforge.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.trainerforge.dto.input.LoginInputDto;
import dev.trainerforge.dto.input.TrainerInputDto;
import dev.trainerforge.dto.response.AuthResponseDto;
import dev.trainerforge.security.jwt.JwtUtil;
import dev.trainerforge.service.TrainerService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final TrainerService trainerService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, TrainerService trainerService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.trainerService = trainerService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginInputDto dto) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        
        String token = jwtUtil.generate(auth.getName());
        
        return ResponseEntity.ok(new AuthResponseDto(token, auth.getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody TrainerInputDto dto) {
        trainerService.createTrainer(dto);
        
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );
        
        String token = jwtUtil.generate(auth.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDto(token, auth.getName()));
    }
}
