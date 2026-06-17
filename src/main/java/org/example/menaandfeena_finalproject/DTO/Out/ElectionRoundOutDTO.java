package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ElectionRoundOutDTO {
    private Integer id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
