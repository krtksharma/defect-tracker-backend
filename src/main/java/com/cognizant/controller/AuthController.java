package com.cognizant.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cognizant.dto.LoginResponse;
import com.cognizant.dto.RegisterRequest;
import com.cognizant.dto.UserDTO;
import com.cognizant.dto.UserPublicDTO;
import com.cognizant.dto.UserRequest;
import com.cognizant.security.JwtUtil;
import com.cognizant.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "Auth + JWT + Registration")
public class AuthController {

    private final UserService userService;
    private final JwtUtil     jwtUtil;
    private final Logger      logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil     = jwtUtil;
    }

    // LOGIN — returns JWT token + user info
    @Operation(description = "Login — returns JWT token + user info")
    @PostMapping("/users/login")
    public ResponseEntity<?> authenticate(@RequestBody UserRequest userRequest) {
        logger.info("Login attempt: {}", userRequest.getUserName());
        UserDTO userDTO = userService.authenticateUser(userRequest.getUserName(), userRequest.getPassword());
        if (userDTO.getUserName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid username or password\"}");
        }
        String token = jwtUtil.generateToken(userDTO.getUserName(), userDTO.getRole());
        return ResponseEntity.ok(new LoginResponse(token, userDTO.getUserName(), userDTO.getRole()));
    }

    // REGISTER
    @Operation(description = "Register new user")
    @PostMapping("/users/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String error = userService.registerUser(request);
        if (error != null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + error + "\"}");
        return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\": \"Account created. You can now sign in.\"}");
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserPublicDTO>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<UserPublicDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersPublic());
    }
}
