package com.networkticket.user.controller;

import com.networkticket.user.model.User;
import com.networkticket.user.service.UserService;
import com.networkticket.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<User> userOpt = userService.login(email, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            return ResponseEntity.ok(Map.of("token", token));
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
    
    @PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        Optional<User> userOpt = userService.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            return ResponseEntity.ok(Map.of("token", token));
        }
        
        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
    }
}