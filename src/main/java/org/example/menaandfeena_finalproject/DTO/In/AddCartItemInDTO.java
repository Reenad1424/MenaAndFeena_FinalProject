package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCartItemInDTO {
    @NotNull(message = "Market place item id cannot be null")
    private Integer marketPlaceItemId;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Positive(message = "Rental days must be greater than zero")
    private Integer rentalDays;
}
