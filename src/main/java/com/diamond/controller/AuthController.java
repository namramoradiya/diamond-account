package com.diamond.controller;

import com.diamond.dto.AuthResponse;
import com.diamond.dto.LoginRequest;
import com.diamond.dto.RegisterRequest;
import com.diamond.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpSession session) {
        AuthResponse response = authService.login(request);
        // Store username in session after successful login
        session.setAttribute("loggedInUser", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "success", true
        ));
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        String user = (String) session.getAttribute("loggedInUser");
        if (user != null) {
            return ResponseEntity.ok(Map.of(
                    "loggedIn", true,
                    "username", user
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("loggedIn", false));
    }
}