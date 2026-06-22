package org.example.menaandfeena_finalproject.DTO.Out;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderDTO {


    private Integer orderId;

    private Integer orderItemId;

    private String productTitle;

    private String buyerName;

    private String sellerName;

    private String orderType;

    private String productStatus;

    private Integer quantity;

    private Integer unitPrice;

    private Integer rentalDays;

    private Integer depositAmount;

    private Integer totalAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    private String returnStatus;
}