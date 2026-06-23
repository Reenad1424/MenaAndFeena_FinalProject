package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Integer maxParticipants;
    private String status;
    private String category;
    private String creatorName;
    private String neighborhoodName;
}
