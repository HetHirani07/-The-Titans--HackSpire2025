package com.MindMosaic.MindMosaic.Service;

import com.MindMosaic.MindMosaic.DTO.AuthResponse;
import com.MindMosaic.MindMosaic.DTO.LoginRequest;
import com.MindMosaic.MindMosaic.Model.User;
import com.MindMosaic.MindMosaic.Model.UserRole;
import com.MindMosaic.MindMosaic.Repository.UserRepository;
import com.MindMosaic.MindMosaic.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse loginOrRegister(LoginRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            return login(request);
        } else {
            return register(request);
        }
    }

    private AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }

    private AuthResponse register(LoginRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}