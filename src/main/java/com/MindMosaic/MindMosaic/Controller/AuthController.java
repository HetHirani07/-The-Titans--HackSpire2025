package com.MindMosaic.MindMosaic.Controller;

import com.MindMosaic.MindMosaic.DTO.AuthResponse;
import com.MindMosaic.MindMosaic.DTO.LoginRequest;
import com.MindMosaic.MindMosaic.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

    public class AuthController {
        private final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
            try {
                return ResponseEntity.ok(authService.loginOrRegister(request));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
    }



