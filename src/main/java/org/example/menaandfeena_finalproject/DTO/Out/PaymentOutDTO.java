package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentOutDTO {
    private Integer id;
    private double amount;
    private double platformFee;
    private double sellerAmount;
    private String status;
    private String transactionId;
}
