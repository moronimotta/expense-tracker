package com.expensetracker.app.controllers;

import com.expensetracker.app.models.User;
import com.expensetracker.app.repositories.UserRepository;
import com.expensetracker.app.services.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    public AuthController(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) throws Exception {
        if (req == null || req.email == null || req.password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "email and password are required"));
        }
        List<User> all = userRepository.findAll();
        for (User u : all) {
            if (req.email.equalsIgnoreCase(u.getEmail()) && req.password.equals(u.getPassword())) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("token", u.getId()); // simple token: userId
                resp.put("user", u);
                return ResponseEntity.ok(resp);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) throws Exception {
        if (req == null || req.email == null || req.password == null || req.name == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "name, email and password are required"));
        }
        User u = new User(req.name, req.email, req.password);
        User saved = userRepository.createUser(u);
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", saved.getId());
        resp.put("user", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        try {
            User u = securityService.getCurrentUser();
            return ResponseEntity.ok(u);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", se.getMessage()));
        }
    }
}
