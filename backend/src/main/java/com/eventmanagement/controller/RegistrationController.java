package com.eventmanagement.controller;

import com.eventmanagement.config.JwtProvider;
import com.eventmanagement.dto.ApiResponse;
import com.eventmanagement.service.RegistrationService;
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
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Object>> registerForEvent(@PathVariable Long eventId) {
        log.info("Registering for event: {}", eventId);
        Long userId = extractUserIdFromToken();
        registrationService.registerForEvent(eventId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Object>builder()
                    .success(true)
                    .message("Event registration successful")
                    .data(null)
                    .build());
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<ApiResponse<Object>> cancelRegistration(@PathVariable Long eventId) {
        log.info("Cancelling registration for event: {}", eventId);
        Long userId = extractUserIdFromToken();
        registrationService.cancelRegistration(eventId, userId);
        return ResponseEntity.ok(ApiResponse.<Object>builder()
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
