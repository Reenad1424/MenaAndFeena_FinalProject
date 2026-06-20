package org.example.menaandfeena_finalproject.DTO.Out;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderDTO {

    private Integer orderId;

    private String productTitle;

    private String buyerName;

    private String sellerName;

    private String orderType;

    private String status;

    private Integer totalAmount;

}