package org.example.menaandfeena_finalproject.DTO.Out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPaymentStatusOutDTO {
    private Integer orderId;
    private Integer paymentId;
    private String moyasarPaymentId;
    private String status;
    private String transactionUrl;
    private String message;
}
