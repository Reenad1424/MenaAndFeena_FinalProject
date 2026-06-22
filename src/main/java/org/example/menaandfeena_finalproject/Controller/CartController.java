package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.AddCartItemInDTO;
import org.example.menaandfeena_finalproject.DTO.In.UpdateCartItemRentalDaysInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addCart(@AuthenticationPrincipal User user) {
        cartService.addCart(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Cart added"));
    }

    // ADMIN/DEBUG
    @GetMapping("/get")
    public ResponseEntity<?> getAllCarts() {
        return ResponseEntity.status(200).body(cartService.getAllCarts());
    }

    @GetMapping("/view")
    public ResponseEntity<?> viewCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(cartService.viewCart(user.getId()));
    }

    // ADMIN/DEBUG
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable Integer id) {
        cartService.deleteCart(id);
        return ResponseEntity.status(200).body(new ApiResponse("Cart deleted"));
    }

    @PostMapping("/items/add")
    public ResponseEntity<?> addItemToCart(@AuthenticationPrincipal User user,
                                           @RequestBody @Valid AddCartItemInDTO addCartItemInDTO) {
        return ResponseEntity.status(200).body(cartService.addItemToCart(user.getId(), addCartItemInDTO));
    }

    @DeleteMapping("/items/remove/{cartItemId}")
    public ResponseEntity<?> removeItemFromCart(@AuthenticationPrincipal User user,
                                                @PathVariable Integer cartItemId) {
        cartService.removeItemFromCart(user.getId(), cartItemId);
        return ResponseEntity.status(200).body(new ApiResponse("Cart item removed"));
    }

    @PutMapping("/items/{cartItemId}/rental-days")
    public ResponseEntity<?> updateRentalDays(@AuthenticationPrincipal User user,
                                              @PathVariable Integer cartItemId,
                                              @RequestBody @Valid UpdateCartItemRentalDaysInDTO updateCartItemRentalDaysInDTO) {
        cartService.updateRentalDays(user.getId(), cartItemId, updateCartItemRentalDaysInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Cart item rental days updated"));
    }
}
