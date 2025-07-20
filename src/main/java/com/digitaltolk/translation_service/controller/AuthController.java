package com.digitaltolk.translation_service.controller;

import com.digitaltolk.translation_service.auth.JwtTokenProvider;
import com.digitaltolk.translation_service.dao.User;
import com.digitaltolk.translation_service.dto.UserDto;
import com.digitaltolk.translation_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login & Register Users")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Returns JWT token on success")
    public ResponseEntity<String> login(@RequestBody UserDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        String token = jwtTokenProvider.createToken(request.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a user", description = "Creates a new user and returns user data")
    public ResponseEntity<User> register(@RequestBody UserDto request) {
        User newUser = userService.createUser(
                request.getUsername(),
                request.getPassword()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}