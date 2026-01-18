package com.eventmanagement.service;

import com.eventmanagement.entity.*;
import com.eventmanagement.exception.BadRequestException;
import com.eventmanagement.exception.ResourceNotFoundException;
import com.eventmanagement.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class RegistrationService {

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public void registerForEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if already registered
        if (registrationRepository.findByEventAndUser(event, user).isPresent()) {
            throw new BadRequestException("User already registered for this event");
        }

        // Check capacity
        if (event.getRegisteredCount() >= event.getCapacity()) {
            throw new BadRequestException("Event is at full capacity");
        }

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(user)
                .status(RegistrationStatus.REGISTERED)
                .build();

        registrationRepository.save(registration);
        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventRepository.save(event);

        // Send confirmation email
        emailService.sendRegistrationConfirmation(user.getEmail(), user.getFirstName(), event.getTitle());

        log.info("User {} registered for event {}", userId, eventId);
    }

    public void cancelRegistration(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        EventRegistration registration = registrationRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new BadRequestException("Registration not found"));

        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);

        event.setRegisteredCount(Math.max(0, event.getRegisteredCount() - 1));
        eventRepository.save(event);

        log.info("User {} cancelled registration for event {}", userId, eventId);
    }

    public List<EventRegistration> getUserRegistrations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return registrationRepository.findByUser(user);
    }

    public List<EventRegistration> getEventRegistrations(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return registrationRepository.findByEvent(event);
    }

    public void markAsAttended(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        EventRegistration registration = registrationRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new BadRequestException("Registration not found"));

        registration.setStatus(RegistrationStatus.ATTENDED);
        registrationRepository.save(registration);

        // Create attendance record
        EventAttendance attendance = EventAttendance.builder()
                .event(event)
                .user(user)
                .build();
        attendanceRepository.save(attendance);

        log.info("User {} marked as attended for event {}", userId, eventId);
    }

    @Autowired
    private EventAttendanceRepository attendanceRepository;
}
