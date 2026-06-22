package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.OrderItemInDTO;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    // TODO SECURITY: These manual OrderItem CRUD endpoints must be ADMIN/DEBUG only after Spring Security.
    // Normal users should create order items only through cart checkout so payment, stock, and insurance logic is not bypassed.
    @PostMapping("/add")
    public ResponseEntity<?> addOrderItem(@RequestBody @Valid OrderItemInDTO orderItemInDTO) {
        orderItemService.addOrderItem(orderItemInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Order item added"));
    }

    // TODO SECURITY: ADMIN/DEBUG only. This exposes all order items across users.
    @GetMapping("/get")
    public ResponseEntity<?> getAllOrderItems() {
        return ResponseEntity.status(200).body(orderItemService.getAllOrderItems());
    }

    // TODO SECURITY: ADMIN/DEBUG only. Do not expose direct order item updates to normal users.
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrderItem(@PathVariable Integer id, @RequestBody @Valid OrderItemInDTO orderItemInDTO) {
        orderItemService.updateOrderItem(id, orderItemInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Order item updated"));
    }

    @PutMapping("/{orderItemId}/renter-confirm-return")
    public ResponseEntity<?> renterConfirmReturn(@PathVariable Integer orderItemId,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(orderItemService.renterConfirmReturn(orderItemId, user.getId()));
    }

    @PutMapping("/{orderItemId}/owner-confirm-return")
    public ResponseEntity<?> ownerConfirmReturn(@PathVariable Integer orderItemId,
                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(orderItemService.ownerConfirmReturn(orderItemId, user.getId()));
    }

    @PutMapping("/{orderItemId}/owner-confirm-damaged")
    public ResponseEntity<?> ownerConfirmDamaged(@PathVariable Integer orderItemId,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(orderItemService.ownerConfirmDamaged(orderItemId, user.getId()));
    }

    // TODO SECURITY: ADMIN/DEBUG only. Deleting order items manually can break checkout/payment history.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable Integer id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.status(200).body(new ApiResponse("Order item deleted"));
    }
}
