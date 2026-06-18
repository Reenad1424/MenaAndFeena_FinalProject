package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InsuranceOutDTO {
    private Integer id;
    private Integer depositAmount;
    private Integer refundedAmount;
    private String status;
    private LocalDateTime heldAt;
    private LocalDateTime refundedAt;
    private String refundTransactionId;
}
