package org.example.menaandfeena_finalproject.DTO.Out;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderOutDTO {
    private Integer id;
    private String invoiceNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;
    private String status;
    private Integer totalAmount;
    private Integer userId;
    private List<OrderItemOutDTO> orderItems;
}
