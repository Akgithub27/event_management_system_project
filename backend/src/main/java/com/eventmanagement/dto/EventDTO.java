package com.eventmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String venue;
    private String category;
    private Integer capacity;
    private Integer registeredCount;
    private String createdByName;
    private Boolean isRegistered;
    private List<SpeakerDTO> speakers;
    private LocalDateTime createdAt;
}
