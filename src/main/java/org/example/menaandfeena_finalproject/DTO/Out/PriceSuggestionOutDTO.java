package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PriceSuggestionOutDTO {

    // Type the suggestion was generated for (SELL or RENT)
    private String type;

    // Suggested selling price (used when type = SELL)
    private Integer suggestedPrice;

    // Suggested rent price and deposit (used when type = RENT)
    private Integer suggestedRentPrice;
    private Integer suggestedDeposit;

    // Range observed across comparable items in the same neighborhood
    private Integer minComparablePrice;
    private Integer maxComparablePrice;

    // How many comparable items in the neighborhood the suggestion is based on
    private Integer comparableCount;

    // Short Arabic explanation of the suggestion
    private String reason;
}
