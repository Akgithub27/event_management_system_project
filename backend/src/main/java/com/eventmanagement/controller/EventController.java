package com.eventmanagement.controller;

import com.eventmanagement.config.JwtProvider;
import com.eventmanagement.dto.ApiResponse;
import com.eventmanagement.dto.CreateEventRequest;
import com.eventmanagement.dto.EventDTO;
import com.eventmanagement.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import java.util.List;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@Tag(name = "Events", description = "Event management endpoints")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping
    @Operation(summary = "Get all events", description = "Retrieve all active events with pagination and filtering")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<List<EventDTO>>> getAllEvents() {
        log.info("Getting all events");
        Long userId = getUserIdFromToken();
        List<EventDTO> events = eventService.getAllEvents(userId);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<List<EventDTO>>builder()
                .success(true)
                .message("Events retrieved successfully")
                .data(events)
                .build());
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events", description = "Retrieve events scheduled for future dates")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Upcoming events retrieved successfully")
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<List<EventDTO>>> getUpcomingEvents() {
        log.info("Getting upcoming events");
        Long userId = getUserIdFromToken();
        List<EventDTO> events = eventService.getUpcomingEvents(userId);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<List<EventDTO>>builder()
                .success(true)
                .message("Upcoming events retrieved successfully")
                .data(events)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Retrieve detailed information about a specific event")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Event retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<EventDTO>> getEventById(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        log.info("Getting event with id: {}", id);
        Long userId = getUserIdFromToken();
        EventDTO event = eventService.getEventById(id, userId);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<EventDTO>builder()
                .success(true)
                .message("Event retrieved successfully")
                .data(event)
                .build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search events", description = "Search events by title or category")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Events searched successfully")
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<List<EventDTO>>> searchEvents(
            @Parameter(description = "Search query") @RequestParam String q) {
        log.info("Searching events with query: {}", q);
        Long userId = getUserIdFromToken();
        List<EventDTO> events = eventService.searchEvents(q, userId);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<List<EventDTO>>builder()
                .success(true)
                .message("Events searched successfully")
                .data(events)
                .build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get events by category", description = "Retrieve events filtered by category")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Events retrieved by category successfully")
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<List<EventDTO>>> getEventsByCategory(
            @Parameter(description = "Event category") @PathVariable String category) {
        log.info("Getting events by category: {}", category);
        Long userId = getUserIdFromToken();
        List<EventDTO> events = eventService.getEventsByCategory(category, userId);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<List<EventDTO>>builder()
                .success(true)
                .message("Events retrieved by category successfully")
                .data(events)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create new event", description = "Create a new event (admin only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Event created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or user not admin"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<EventDTO>> createEvent(@RequestBody CreateEventRequest request) {
        log.info("Creating new event: {}", request.getTitle());
        Long userId = getUserIdFromToken();
        EventDTO event = eventService.createEvent(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.eventmanagement.dto.ApiResponse.<EventDTO>builder()
                    .success(true)
                    .message("Event created successfully")
                    .data(event)
                    .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update event", description = "Update an existing event (admin or creator only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event updated successfully"),
        @ApiResponse(responseCode = "403", description = "Permission denied"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<EventDTO>> updateEvent(
            @Parameter(description = "Event ID") @PathVariable Long id,
            @RequestBody CreateEventRequest request) {
        log.info("Updating event: {}", id);
        Long userId = getUserIdFromToken();
        EventDTO event = eventService.updateEvent(id, request, userId);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<EventDTO>builder()
                .success(true)
                .message("Event updated successfully")
                .data(event)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete event", description = "Delete an event (admin or creator only)")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Permission denied"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<com.eventmanagement.dto.ApiResponse<Object>> deleteEvent(
            @Parameter(description = "Event ID") @PathVariable Long id) {
        log.info("Deleting event: {}", id);
        Long userId = getUserIdFromToken();
        eventService.deleteEvent(id, userId);
        return ResponseEntity.ok(com.eventmanagement.dto.ApiResponse.<Object>builder()
                .success(true)
                .message("Event deleted successfully")
                .data(null)
                .build());
    }

    private Long getUserIdFromToken() {
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
