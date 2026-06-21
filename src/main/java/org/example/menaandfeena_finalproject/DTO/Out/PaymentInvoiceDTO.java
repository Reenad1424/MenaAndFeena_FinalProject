package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class PaymentInvoiceDTO {

        private String moyasarPaymentId;

        private String paymentStatus;

        private String approvalMessage;

        private String description;

        private String eventTitle;

        private String amount;

        private String currency;

        private String fee;

        private String refundedAmount;

        private String capturedAmount;

        private String cardCompany;

        private String cardHolderName;

        private String issuerName;

        private String issuerCountry;

        private String createdAt;
    }

