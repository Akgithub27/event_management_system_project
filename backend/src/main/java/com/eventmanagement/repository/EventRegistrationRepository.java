package com.eventmanagement.repository;

import com.eventmanagement.entity.EventRegistration;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.User;
import com.eventmanagement.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Optional<EventRegistration> findByEventAndUser(Event event, User user);
    List<EventRegistration> findByEvent(Event event);
    List<EventRegistration> findByUser(User user);
    List<EventRegistration> findByEventAndStatus(Event event, RegistrationStatus status);
    List<EventRegistration> findByUserAndStatus(User user, RegistrationStatus status);
    Long countByEventAndStatus(Event event, RegistrationStatus status);
}
