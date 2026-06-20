package org.example.menaandfeena_finalproject.DTO.Out;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMarketItemDTO {

    private Integer id;

    private String title;

    private String type;

    private String status;

    private Integer quantity;

    private Integer price;

    private Integer rentPrice;

}