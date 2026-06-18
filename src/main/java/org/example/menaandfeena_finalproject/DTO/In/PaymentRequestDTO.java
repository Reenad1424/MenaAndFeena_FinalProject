package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Card number cannot be blank")
    private String number;

    @NotBlank(message = "CVC cannot be blank")
    private String cvc;

    @NotBlank(message = "Month cannot be blank")
    private String month;

    @NotBlank(message = "Year cannot be blank")
    private String year;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Integer amount;

    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    private String description;

    private String callbackurl;
}
