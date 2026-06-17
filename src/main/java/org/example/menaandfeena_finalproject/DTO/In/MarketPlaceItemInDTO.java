package org.example.menaandfeena_finalproject.DTO.In;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketPlaceItemInDTO {
    private String title;
    private String description;
    private String type;
    private String status;
    private double price;
    private double rentPrice;
    private double depositAmount;
    private Integer quantity;
}
