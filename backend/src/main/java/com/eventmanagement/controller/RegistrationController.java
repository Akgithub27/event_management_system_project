package com.eventmanagement.controller;

import com.eventmanagement.dto.ApiResponse;
import com.eventmanagement.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registrations")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

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
        // This would be extracted from the JWT token in a real implementation
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // For now, return a placeholder - in production, extract from token
        return 1L;
    }
}
