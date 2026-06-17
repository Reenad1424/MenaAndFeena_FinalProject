package org.example.menaandfeena_finalproject.DTO.In;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInDTO {
    private String type;
    private String status;
    private double totalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
}
