package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

// Holds the relevant fields returned by Moyasar when an invoice is created.
@Data
@AllArgsConstructor
public class MoyasarInvoiceOutDTO {
    private String id;
    private String url;
    private String status;
    private Integer amount;
}
