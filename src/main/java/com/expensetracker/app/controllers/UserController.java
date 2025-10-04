package com.expensetracker.app.controllers;

import com.expensetracker.app.dto.ApiResponse;
import com.expensetracker.app.services.SecurityService;
import org.springframework.web.bind.annotation.PathVariable;
import com.expensetracker.app.models.User;
import com.expensetracker.app.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final SecurityService securityService;

    public UserController(UserRepository userRepository, SecurityService securityService) {
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    // GET /users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() throws Exception {
        securityService.requireAdmin();
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) throws Exception {
        User user = userRepository.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /users
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) throws Exception {
        User savedUser = userRepository.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // PUT /users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable String id, @RequestBody User user) throws Exception {
        user.setId(id);
        User savedUser = userRepository.update(user);
        return ResponseEntity.ok(new ApiResponse<>("User updated successfully", savedUser));
    }

    // DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) throws Exception {
        userRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>("User deleted successfully", null));
    }
}