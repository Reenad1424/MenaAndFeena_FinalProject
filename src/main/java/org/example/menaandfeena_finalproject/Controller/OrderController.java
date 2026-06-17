package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.OrderInDTO;
import org.example.menaandfeena_finalproject.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.status(200).body(orderService.getAllOrders());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@RequestBody OrderInDTO orderInDTO) {
        orderService.addOrder(orderInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Order added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Integer id, @RequestBody OrderInDTO orderInDTO) {
        orderService.updateOrder(id, orderInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Order updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(200).body(new ApiResponse("Order deleted"));
    }
}
