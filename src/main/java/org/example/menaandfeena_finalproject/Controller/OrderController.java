package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.Orders;
import org.example.menaandfeena_finalproject.Repository.OrderRepository;
import org.example.menaandfeena_finalproject.Service.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping("/checkout-cart/{userId}")
    public ResponseEntity<?> checkoutCart(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(orderService.checkoutCart(userId));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@RequestParam Integer userId) {
        return ResponseEntity.status(200).body(orderService.getMyOrders(userId));
    }

    @GetMapping("/my-sales")
    public ResponseEntity<?> getMySales(@RequestParam Integer userId) {
        return ResponseEntity.status(200).body(orderService.getMySales(userId));
    }

    @PutMapping("/complete/{orderId}/{userId}")
    public ResponseEntity<?> completeOrder(@PathVariable Integer orderId, @PathVariable Integer userId) {
        orderService.completeOrder(orderId, userId);
        return ResponseEntity.status(200).body(new ApiResponse("Order completed"));
    }

    @PutMapping("/cancel/{orderId}/{userId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer orderId, @PathVariable Integer userId) {
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.status(200).body(new ApiResponse("Order cancelled"));
    }

    // TODO SECURITY: ADMIN/DEBUG only. Normal users should use /my-orders or /my-sales.
    @GetMapping("/get")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.status(200).body(orderService.getAllOrders());
    }

    // TODO SECURITY: ADMIN/DEBUG only. User-facing order details should include user ownership validation.
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.status(200).body(orderService.getOrderById(orderId));
    }

    @GetMapping("/{orderId}/invoice/pdf")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable Integer orderId, @RequestParam Integer userId) {
        Orders order = orderRepository.findOrderById(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }

        byte[] pdfBytes = orderService.generateInvoicePdf(orderId, userId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice-" + order.getInvoiceNumber() + ".pdf\"")
                .body(pdfBytes);
    }

    // TODO SECURITY: ADMIN/DEBUG only. Deleting orders manually can break payment, stock, and invoice history.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(200).body(new ApiResponse("Order deleted"));
    }
}
