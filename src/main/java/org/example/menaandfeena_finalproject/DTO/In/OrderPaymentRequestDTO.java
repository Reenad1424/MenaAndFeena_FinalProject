package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentRequestDTO {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Card number cannot be empty")
    private String number;

    @NotEmpty(message = "CVC cannot be empty")
    private String cvc;

    @NotEmpty(message = "Month cannot be empty")
    private String month;

    @NotEmpty(message = "Year cannot be empty")
    private String year;

}