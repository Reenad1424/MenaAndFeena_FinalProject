package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemInDTO {
    @NotBlank(message = "Type cannot be blank")
    @Pattern(regexp = "SELL|RENT", message = "Type must be SELL or RENT only")
    private String type;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Unit price cannot be null")
    @Positive(message = "Unit price must be positive")
    private Integer unitPrice;

    @Positive(message = "Rental days must be greater than zero")
    private Integer rentalDays;

    @PositiveOrZero(message = "Deposit amount must be zero or positive")
    private Integer depositAmount;

    @NotNull(message = "Subtotal cannot be null")
    @PositiveOrZero(message = "Subtotal must be zero or positive")
    private Integer subtotal;

    @NotNull(message = "Order id cannot be null")
    private Integer orderId;

    @NotNull(message = "Market place item id cannot be null")
    private Integer marketPlaceItemId;
}
