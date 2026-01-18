package com.eventmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEventRequest {
    private String title;
    private String description;
    private String eventDate;
    private String venue;
    private String category;
    private Integer capacity;
}
