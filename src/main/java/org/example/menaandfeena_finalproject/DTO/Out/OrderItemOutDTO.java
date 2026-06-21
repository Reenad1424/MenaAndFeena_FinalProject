package org.example.menaandfeena_finalproject.DTO.Out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemOutDTO {
    private Integer id;
    private String itemName;
    private String type;
    private Integer quantity;
    private Integer unitPrice;
    private Integer subtotal;
    private Integer rentalDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private String returnStatus;
    private Boolean renterConfirmedReturn;
    private Boolean ownerConfirmedReturn;
    private LocalDateTime renterConfirmedReturnAt;
    private LocalDateTime ownerConfirmedReturnAt;
    private InsuranceOutDTO insurance;
}
