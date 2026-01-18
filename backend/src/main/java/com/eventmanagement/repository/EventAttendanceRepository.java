package com.eventmanagement.repository;

import com.eventmanagement.entity.EventAttendance;
import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long> {
    Optional<EventAttendance> findByEventAndUser(Event event, User user);
    List<EventAttendance> findByEvent(Event event);
    List<EventAttendance> findByUser(User user);
    Long countByEvent(Event event);
}
