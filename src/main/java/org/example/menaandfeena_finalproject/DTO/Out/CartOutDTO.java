package org.example.menaandfeena_finalproject.DTO.Out;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartOutDTO {
    private Integer id;
    private Integer userId;
    private List<CartItemOutDTO> cartItems;
}
