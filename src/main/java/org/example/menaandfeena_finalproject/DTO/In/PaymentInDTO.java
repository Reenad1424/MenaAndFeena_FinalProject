package org.example.menaandfeena_finalproject.DTO.In;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInDTO {
    private double amount;
    private double platformFee;
    private double sellerAmount;
    private String status;
    private String transactionId;
}
