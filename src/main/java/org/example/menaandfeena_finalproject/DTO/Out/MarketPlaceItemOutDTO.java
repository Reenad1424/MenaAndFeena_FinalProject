package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
}
