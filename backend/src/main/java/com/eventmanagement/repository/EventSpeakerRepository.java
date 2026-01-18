package com.eventmanagement.repository;

import com.eventmanagement.entity.EventSpeaker;
import com.eventmanagement.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSpeakerRepository extends JpaRepository<EventSpeaker, Long> {
    List<EventSpeaker> findByEvent(Event event);
}
