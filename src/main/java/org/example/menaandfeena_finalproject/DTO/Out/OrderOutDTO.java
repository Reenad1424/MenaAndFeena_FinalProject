package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OrderOutDTO {
    private Integer id;
    private String type;
    private String status;
    private double totalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
}
