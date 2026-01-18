package com.eventmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakerDTO {
    private Long id;
    private String name;
    private String bio;
    private String expertise;
    private String email;
}
