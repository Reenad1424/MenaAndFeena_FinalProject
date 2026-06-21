package org.example.menaandfeena_finalproject.DTO.Out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketPlaceItemOutDTO {
    private Integer id;
    private String title;
    private String description;
    private String type;
    private String status;
    private Integer price;
    private Integer rentPrice;
    private Integer depositAmount;
    private Integer quantity;
    private Integer userId;
    private String sellerFullName;
}
