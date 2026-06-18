package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceInDTO {

    @NotNull(message = "Deposit amount cannot be null")
    @PositiveOrZero(message = "Deposit amount must be zero or positive")
    private Integer depositAmount;

    @NotNull(message = "Refunded amount cannot be null")
    @PositiveOrZero(message = "Refunded amount must be zero or positive")
    private Integer refundedAmount;

    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "HELD|REFUNDED|DEDUCTED", message = "Status must be HELD, REFUNDED, or DEDUCTED")
    private String status;

    private LocalDateTime heldAt;

    private LocalDateTime refundedAt;

    private String refundTransactionId;
}
