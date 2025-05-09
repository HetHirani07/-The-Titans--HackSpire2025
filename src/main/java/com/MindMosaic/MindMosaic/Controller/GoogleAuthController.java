package com.MindMosaic.MindMosaic.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @GetMapping("/success")
    public ResponseEntity<String> googleAuthSuccess() {
        return ResponseEntity.ok("Google Sign-In successful!");
    }
}
