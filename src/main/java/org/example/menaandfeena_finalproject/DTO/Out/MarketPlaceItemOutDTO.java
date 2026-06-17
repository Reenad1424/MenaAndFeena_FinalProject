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
    private double price;
    private double rentPrice;
    private double depositAmount;
    private Integer quantity;
}
