package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartItemRentalDaysInDTO {
    @NotNull(message = "Rental days cannot be null")
    @Positive(message = "Rental days must be positive")
    private Integer rentalDays;
}
