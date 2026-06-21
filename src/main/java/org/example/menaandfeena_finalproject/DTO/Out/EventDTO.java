package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private String status;
    private Boolean isPaid;
    private Double price;
    private Integer maxParticipants;
}