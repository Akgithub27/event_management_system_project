package com.eventmanagement.repository;

import com.eventmanagement.entity.Event;
import com.eventmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreatedBy(User createdBy);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true ORDER BY e.eventDate ASC")
    List<Event> findAllActiveEvents();
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.eventDate >= :currentDate ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(e.category) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Event> searchEvents(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND LOWER(e.category) = LOWER(:category) ORDER BY e.eventDate ASC")
    List<Event> findByCategory(@Param("category") String category);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.venue = :venue")
    List<Event> findByVenue(@Param("venue") String venue);
}
