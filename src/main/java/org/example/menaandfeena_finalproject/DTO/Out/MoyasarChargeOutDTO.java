package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoyasarChargeOutDTO {

//    private String status;
//    private Integer amount;
//    private String transactionUrl;

    private String moyasarPaymentId;
    private String status;
    private Integer amount;
    private String transactionUrl;
}

