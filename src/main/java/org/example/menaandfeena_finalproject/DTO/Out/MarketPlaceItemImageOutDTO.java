package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarketPlaceItemImageOutDTO {
    private Integer id;
    private String imageUrl;
    private Integer marketPlaceItemId;
}
