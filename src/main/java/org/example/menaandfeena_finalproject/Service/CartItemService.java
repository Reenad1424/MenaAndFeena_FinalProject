package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.Out.CartItemOutDTO;
import org.example.menaandfeena_finalproject.Model.CartItem;
import org.example.menaandfeena_finalproject.Repository.CartItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    public List<CartItemOutDTO> getAllCartItems() {
        List<CartItemOutDTO> cartItemOutDTOS = new ArrayList<>();

        for (CartItem cartItem : cartItemRepository.findAll()) {
            Integer cartId = cartItem.getCart() == null ? null : cartItem.getCart().getId();
            Integer marketPlaceItemId = cartItem.getMarketPlaceItem() == null ? null : cartItem.getMarketPlaceItem().getId();
            cartItemOutDTOS.add(new CartItemOutDTO(cartItem.getId(), cartItem.getQuantity(), cartItem.getRentalDays(), cartItem.getStartDate(), cartItem.getEndDate(), cartId, marketPlaceItemId));
        }

        return cartItemOutDTOS;
    }

    public void deleteCartItem(Integer id) {
        CartItem cartItem = cartItemRepository.findCartItemById(id);
        if (cartItem == null) {
            throw new ApiException("Cart item not found");
        }

        cartItemRepository.delete(cartItem);
    }
}
