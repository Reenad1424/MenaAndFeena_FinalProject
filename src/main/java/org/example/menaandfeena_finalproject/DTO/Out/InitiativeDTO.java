package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiativeDTO {
    private Integer id;
    private String title;
    private String description;
    private LocalDate date;
    private String status;
    private Integer maxParticipants;
    private String category;
}