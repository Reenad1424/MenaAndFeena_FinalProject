package org.example.menaandfeena_finalproject.DTO.In;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketPlaceItemImageInDTO {
    @NotBlank(message = "Image URL cannot be blank")
    private String imageUrl;

    private Integer marketPlaceItemId;
}
