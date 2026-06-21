package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentOutDTO {
    private Integer paymentId;
    private Integer amount;
    private Integer platformFee;
    private Integer sellerAmount;
    private String status;
    private String transactionId;
    private String paymentUrl;
    private String description;
}
