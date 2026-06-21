package org.example.menaandfeena_finalproject.DTO.Out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

// Returned by checkoutCart. The user then pays on our own page, which posts card
// details to POST /api/v1/payment/order/{orderId}. No hosted Moyasar URL is involved.
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckoutResponseDTO {
    private Integer orderId;
    private Integer paymentId;
    private Integer totalAmount;
    private String status;
}
