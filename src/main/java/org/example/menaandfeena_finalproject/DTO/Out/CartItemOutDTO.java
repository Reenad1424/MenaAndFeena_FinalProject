package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CartItemOutDTO {
    private Integer id;
    private Integer quantity;
    private Integer rentalDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer cartId;
    private Integer marketPlaceItemId;
}
