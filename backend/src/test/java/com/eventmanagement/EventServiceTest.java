package com.eventmanagement.service;

import com.eventmanagement.dto.CreateEventRequest;
import com.eventmanagement.dto.EventDTO;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.User;
import com.eventmanagement.entity.UserRole;
import com.eventmanagement.exception.BadRequestException;
import com.eventmanagement.exception.ResourceNotFoundException;
import com.eventmanagement.repository.EventRepository;
import com.eventmanagement.repository.UserRepository;
import com.eventmanagement.repository.EventSpeakerRepository;
import com.eventmanagement.repository.EventRegistrationRepository;
import com.eventmanagement.repository.EventAttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventSpeakerRepository eventSpeakerRepository;

    @Mock
    private EventRegistrationRepository registrationRepository;

    @Mock
    private EventAttendanceRepository attendanceRepository;

    @InjectMocks
    private EventService eventService;

    private User adminUser;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = User.builder()
                .id(1L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();

        testEvent = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Test Description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .venue("Test Venue")
                .category("Tech")
                .capacity(100)
                .registeredCount(10)
                .createdBy(adminUser)
                .isActive(true)
                .build();
    }

    @Test
    void testCreateEventSuccess() {
        CreateEventRequest request = CreateEventRequest.builder()
                .title("New Event")
                .description("Description")
                .eventDate(LocalDateTime.now().plusDays(1).toString())
                .venue("Venue")
                .category("Tech")
                .capacity(100)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventSpeakerRepository.findByEvent(any())).thenReturn(Collections.emptyList());

        EventDTO result = eventService.createEvent(request, 1L);

        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCreateEventWithoutAdminRole() {
        User regularUser = User.builder()
                .id(2L)
                .email("user@example.com")
                .role(UserRole.USER)
                .build();

        CreateEventRequest request = CreateEventRequest.builder()
                .title("New Event")
                .description("Description")
                .eventDate(LocalDateTime.now().plusDays(1).toString())
                .venue("Venue")
                .category("Tech")
                .capacity(100)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(regularUser));

        assertThrows(BadRequestException.class, () -> eventService.createEvent(request, 2L));
    }

    @Test
    void testGetAllEvents() {
        List<Event> events = Collections.singletonList(testEvent);
        when(eventRepository.findAllActiveEvents()).thenReturn(events);
        when(eventSpeakerRepository.findByEvent(testEvent)).thenReturn(Collections.emptyList());

        List<EventDTO> result = eventService.getAllEvents(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findAllActiveEvents();
    }

    @Test
    void testGetEventByIdNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(999L, 1L));
    }

    @Test
    void testSearchEvents() {
        List<Event> events = Collections.singletonList(testEvent);
        when(eventRepository.searchEvents("Test")).thenReturn(events);
        when(eventSpeakerRepository.findByEvent(testEvent)).thenReturn(Collections.emptyList());

        List<EventDTO> result = eventService.searchEvents("Test", 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).searchEvents("Test");
    }
}
