package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllCartItems() {
        return ResponseEntity.status(200).body(cartItemService.getAllCartItems());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Integer id) {
        cartItemService.deleteCartItem(id);
        return ResponseEntity.status(200).body(new ApiResponse("Cart item deleted"));
    }
}
