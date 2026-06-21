package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.AddCartItemInDTO;
import org.example.menaandfeena_finalproject.DTO.In.UpdateCartItemRentalDaysInDTO;
import org.example.menaandfeena_finalproject.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addCart(@PathVariable Integer userId) {
        cartService.addCart(userId);
        return ResponseEntity.status(200).body(new ApiResponse("Cart added"));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllCarts() {
        return ResponseEntity.status(200).body(cartService.getAllCarts());
    }

    @GetMapping("/view/{userId}")
    public ResponseEntity<?> viewCart(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(cartService.viewCart(userId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable Integer id) {
        cartService.deleteCart(id);
        return ResponseEntity.status(200).body(new ApiResponse("Cart deleted"));
    }

    @PostMapping("/{userId}/items/add")
    public ResponseEntity<?> addItemToCart(@PathVariable Integer userId, @RequestBody @Valid AddCartItemInDTO addCartItemInDTO) {
        return ResponseEntity.status(200).body(cartService.addItemToCart(userId, addCartItemInDTO));
    }

    @DeleteMapping("/{userId}/items/remove/{cartItemId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Integer userId, @PathVariable Integer cartItemId) {
        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.status(200).body(new ApiResponse("Cart item removed"));
    }

    @PutMapping("/{userId}/items/{cartItemId}/rental-days")
    public ResponseEntity<?> updateRentalDays(@PathVariable Integer userId, @PathVariable Integer cartItemId, @RequestBody @Valid UpdateCartItemRentalDaysInDTO updateCartItemRentalDaysInDTO) {
        cartService.updateRentalDays(userId, cartItemId, updateCartItemRentalDaysInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Cart item rental days updated"));
    }
}
