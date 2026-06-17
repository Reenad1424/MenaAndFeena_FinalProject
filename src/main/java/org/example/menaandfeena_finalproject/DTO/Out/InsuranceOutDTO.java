package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsuranceOutDTO {
    private Integer id;
    private double depositAmount;
    private double refundedAmount;
    private String status;
}
