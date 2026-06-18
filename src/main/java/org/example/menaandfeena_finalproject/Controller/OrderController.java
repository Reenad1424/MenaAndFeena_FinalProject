package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.OrderInDTO;
import org.example.menaandfeena_finalproject.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/buy/{itemId}")
    public ResponseEntity<?> buyItem(@PathVariable Integer itemId, @RequestParam Integer userId) {
        return ResponseEntity.status(200).body(orderService.buyItem(itemId, userId));
    }

    @PostMapping("/rent/{itemId}")
    public ResponseEntity<?> rentItem(@PathVariable Integer itemId, @RequestBody @Valid OrderInDTO orderInDTO) {
        return ResponseEntity.status(200).body(orderService.rentItem(itemId, orderInDTO));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@RequestParam Integer userId) {
        return ResponseEntity.status(200).body(orderService.getMyOrders(userId));
    }

    @GetMapping("/my-sales")
    public ResponseEntity<?> getMySales(@RequestParam Integer userId) {
        return ResponseEntity.status(200).body(orderService.getMySales(userId));
    }

    @PutMapping("/complete/{orderId}")
    public ResponseEntity<?> completeOrder(@PathVariable Integer orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.status(200).body(new ApiResponse("Order completed"));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.status(200).body(new ApiResponse("Order cancelled"));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.status(200).body(orderService.getAllOrders());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(200).body(new ApiResponse("Order deleted"));
    }
}
