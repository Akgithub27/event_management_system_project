package com.eventmanagement.service;

import com.eventmanagement.dto.CreateEventRequest;
import com.eventmanagement.dto.EventDTO;
import com.eventmanagement.dto.SpeakerDTO;
import com.eventmanagement.entity.*;
import com.eventmanagement.exception.BadRequestException;
import com.eventmanagement.exception.ResourceNotFoundException;
import com.eventmanagement.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpeakerRepository speakerRepository;

    @Autowired
    private EventSpeakerRepository eventSpeakerRepository;

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private EventAttendanceRepository attendanceRepository;

    public EventDTO createEvent(CreateEventRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new BadRequestException("Only admins can create events");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime eventDate = LocalDateTime.parse(request.getEventDate(), formatter);

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(eventDate)
                .venue(request.getVenue())
                .category(request.getCategory())
                .capacity(request.getCapacity())
                .createdBy(user)
                .isActive(true)
                .build();

        Event savedEvent = eventRepository.save(event);
        log.info("Event created: {} by user: {}", savedEvent.getId(), userId);

        return mapToDTO(savedEvent, null);
    }

    public List<EventDTO> getAllEvents(Long userId) {
        List<Event> events = eventRepository.findAllActiveEvents();
        return events.stream()
                .map(event -> {
                    EventRegistration reg = null;
                    if (userId != null) {
                        reg = registrationRepository.findByEventAndUser(event, userRepository.findById(userId).orElse(null))
                                .orElse(null);
                    }
                    return mapToDTO(event, userId);
                })
                .collect(Collectors.toList());
    }

    public List<EventDTO> getUpcomingEvents(Long userId) {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDateTime.now());
        return events.stream()
                .map(event -> mapToDTO(event, userId))
                .collect(Collectors.toList());
    }

    public EventDTO getEventById(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return mapToDTO(event, userId);
    }

    public List<EventDTO> searchEvents(String searchTerm, Long userId) {
        List<Event> events = eventRepository.searchEvents(searchTerm);
        return events.stream()
                .map(event -> mapToDTO(event, userId))
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByCategory(String category, Long userId) {
        List<Event> events = eventRepository.findByCategory(category);
        return events.stream()
                .map(event -> mapToDTO(event, userId))
                .collect(Collectors.toList());
    }

    public EventDTO updateEvent(Long eventId, CreateEventRequest request, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!event.getCreatedBy().getId().equals(userId) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to update this event");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime eventDate = LocalDateTime.parse(request.getEventDate(), formatter);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(eventDate);
        event.setVenue(request.getVenue());
        event.setCategory(request.getCategory());
        event.setCapacity(request.getCapacity());

        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated: {} by user: {}", eventId, userId);

        return mapToDTO(updatedEvent, userId);
    }

    public void deleteEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!event.getCreatedBy().getId().equals(userId) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new BadRequestException("You don't have permission to delete this event");
        }

        event.setIsActive(false);
        eventRepository.save(event);
        log.info("Event deleted: {} by user: {}", eventId, userId);
    }

    private EventDTO mapToDTO(Event event, Long userId) {
        Boolean isRegistered = false;
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            isRegistered = registrationRepository.findByEventAndUser(event, user).isPresent();
        }

        List<SpeakerDTO> speakers = eventSpeakerRepository.findByEvent(event).stream()
                .map(es -> mapSpeakerToDTO(es.getSpeaker()))
                .collect(Collectors.toList());

        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .venue(event.getVenue())
                .category(event.getCategory())
                .capacity(event.getCapacity())
                .registeredCount(event.getRegisteredCount())
                .createdByName(event.getCreatedBy().getFirstName() + " " + event.getCreatedBy().getLastName())
                .isRegistered(isRegistered)
                .speakers(speakers)
                .createdAt(event.getCreatedAt())
                .build();
    }

    private SpeakerDTO mapSpeakerToDTO(Speaker speaker) {
        return SpeakerDTO.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .bio(speaker.getBio())
                .expertise(speaker.getExpertise())
                .email(speaker.getEmail())
                .build();
    }
}
