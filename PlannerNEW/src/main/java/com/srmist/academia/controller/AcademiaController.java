package com.srmist.academia.controller;

import com.srmist.academia.dto.UserRegisterRequest;
import com.srmist.academia.dto.UserLoginRequest;
import com.srmist.academia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AcademiaController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated UserRegisterRequest request) {
        try {
            userService.registerUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(Map.of( "message", "Registration successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated UserLoginRequest request) {
        try {
            String sessionToken = userService.loginUser(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(Map.of("sessionToken", sessionToken, "message", "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
