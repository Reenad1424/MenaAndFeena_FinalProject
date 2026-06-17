package org.example.menaandfeena_finalproject.DTO.In;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceInDTO {
    private double depositAmount;
    private double refundedAmount;
    private String status;
}
