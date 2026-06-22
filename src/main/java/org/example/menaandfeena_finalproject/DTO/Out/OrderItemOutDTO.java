package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String returnStatus;
    private Boolean renterConfirmedReturn;
    private Boolean ownerConfirmedReturn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime renterConfirmedReturnAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ownerConfirmedReturnAt;
    private InsuranceOutDTO insurance;
}
