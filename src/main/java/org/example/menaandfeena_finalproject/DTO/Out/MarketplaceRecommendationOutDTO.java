package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarketplaceRecommendationOutDTO {
    private Integer itemId;
    private String title;
    private String description;
    private String type;
    private Integer price;
    private Integer rentPrice;
    private Integer depositAmount;
    private Integer quantity;
    private String reason;
}
