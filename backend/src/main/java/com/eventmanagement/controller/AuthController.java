package com.eventmanagement.controller;

import com.eventmanagement.dto.*;
import com.eventmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register new user", description = "Create a new user account with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully",
                content = @Content(schema = @Schema(implementation = SignupResponse.class))),
        @ApiResponse(responseCode = "400", description = "Email already registered or invalid input"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<SignupResponse>> signup(@RequestBody SignupRequest request) {
        log.info("Signup request for email: {}", request.getEmail());
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.eventmanagement.dto.ApiResponse.<SignupResponse>builder()
                    .success(true)
                    .message("User registered successfully")
                    .data(response)
                    .build());
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password, returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid email or password"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build());
    }
}
