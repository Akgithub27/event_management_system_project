package com.eventmanagement.controller;

import com.eventmanagement.config.JwtProvider;
import com.eventmanagement.dto.ApiResponse;
import com.eventmanagement.dto.CreateEventRequest;
import com.eventmanagement.dto.EventDTO;
import com.eventmanagement.service.EventService;
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
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents() {
        log.info("Getting all events");
        Long userId = getUserIdFromToken();
        List<EventDTO> events = eventService.getAllEvents(userId);
        return ResponseEntity.ok(ApiResponse.<List<EventDTO>>builder()
                .success(true)
                .message("Events retrieved successfully")
                .data(events)
                .build());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getUpcomingEvents() {
        log.info("Getting upcoming events");
        Long userId = getUserIdFromToken();
        List<EventDTO> events = eventService.getUpcomingEvents(userId);
        return ResponseEntity.ok(ApiResponse.<List<EventDTO>>builder()
                .success(true)
                .message("Upcoming events retrieved successfully")
                .data(events)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable Long id) {
        log.info("Getting event with id: {}", id);
        Long userId = getUserIdFromToken();
        EventDTO event = eventService.getEventById(id, userId);
        return ResponseEntity.ok(ApiResponse.<EventDTO>builder()
                .success(true)
                .message("Event retrieved successfully")
                .data(event)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EventDTO>>> searchEvents(@RequestParam String q) {
        log.info("Searching events with query: {}", q);
        Long userId = getUserIdFromToken();
        List<EventDTO> events = eventService.searchEvents(q, userId);
        return ResponseEntity.ok(ApiResponse.<List<EventDTO>>builder()
                .success(true)
                .message("Events searched successfully")
                .data(events)
                .build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByCategory(@PathVariable String category) {
        log.info("Getting events by category: {}", category);
        Long userId = getUserIdFromToken();
        List<EventDTO> events = eventService.getEventsByCategory(category, userId);
        return ResponseEntity.ok(ApiResponse.<List<EventDTO>>builder()
                .success(true)
                .message("Events retrieved by category successfully")
                .data(events)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(@RequestBody CreateEventRequest request) {
        log.info("Creating new event: {}", request.getTitle());
        Long userId = getUserIdFromToken();
        EventDTO event = eventService.createEvent(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EventDTO>builder()
                    .success(true)
                    .message("Event created successfully")
                    .data(event)
                    .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(
            @PathVariable Long id,
            @RequestBody CreateEventRequest request) {
        log.info("Updating event: {}", id);
        Long userId = getUserIdFromToken();
        EventDTO event = eventService.updateEvent(id, request, userId);
        return ResponseEntity.ok(ApiResponse.<EventDTO>builder()
                .success(true)
                .message("Event updated successfully")
                .data(event)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteEvent(@PathVariable Long id) {
        log.info("Deleting event: {}", id);
        Long userId = getUserIdFromToken();
        eventService.deleteEvent(id, userId);
        return ResponseEntity.ok(ApiResponse.<Object>builder()
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
