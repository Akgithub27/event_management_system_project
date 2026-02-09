package com.eventmanagement.controller;

import com.eventmanagement.config.JwtProvider;
import com.eventmanagement.dto.ApiResponse;
import com.eventmanagement.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/registrations")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@Tag(name = "Event Registration", description = "Event registration management endpoints")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/events/{eventId}")
    @Operation(summary = "Register for event", description = "Register the authenticated user for a specific event")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Event registration successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or already registered"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<Object>> registerForEvent(
            @Parameter(description = "Event ID") @PathVariable Long eventId) {
        log.info("Registering for event: {}", eventId);
        Long userId = extractUserIdFromToken();
        registrationService.registerForEvent(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.eventmanagement.dto.ApiResponse.<Object>builder()
                    .success(true)
                    .message("Event registration successful")
                    .data(null)
                    .build());
    }

    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "Cancel event registration", description = "Cancel the user's registration for a specific event")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration cancelled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Event or registration not found")
    })
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<Object>> cancelRegistration(
            @Parameter(description = "Event ID") @PathVariable Long eventId) {
        log.info("Cancelling registration for event: {}", eventId);
        Long userId = extractUserIdFromToken();
        registrationService.cancelRegistration(eventId, userId);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<Object>builder()
                .success(true)
                .message("Registration cancelled successfully")
                .data(null)
                .build());
    }

    private Long extractUserIdFromToken() {
        try {
            String token = getTokenFromRequest();
            if (token != null && jwtProvider.validateToken(token)) {
                return jwtProvider.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            log.error("Error extracting userId from token", e);
        }
        return null;
    }

    private String getTokenFromRequest() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        } catch (Exception e) {
            log.debug("Could not extract token from request", e);
        }
        return null;
    }
}
