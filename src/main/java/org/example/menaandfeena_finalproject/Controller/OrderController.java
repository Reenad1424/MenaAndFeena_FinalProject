package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.OrderPaymentRequestDTO;
import org.example.menaandfeena_finalproject.Model.Orders;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.OrderRepository;
import org.example.menaandfeena_finalproject.Service.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping("/checkout-cart")
    public ResponseEntity<?> checkoutCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(orderService.checkoutCart(user.getId()));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(orderService.getMyOrders(user.getId()));
    }

    @GetMapping("/my-sales")
    public ResponseEntity<?> getMySales(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(orderService.getMySales(user.getId()));
    }

    @PutMapping("/complete/{orderId}")
    public ResponseEntity<?> completeOrder(@PathVariable Integer orderId,
                                           @AuthenticationPrincipal User user) {
        orderService.completeOrder(orderId, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Order completed"));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer orderId,
                                         @AuthenticationPrincipal User user) {
        orderService.cancelOrder(orderId, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Order cancelled"));
    }

    @PostMapping("/{orderId}/payment")
    public ResponseEntity<?> payOrderWithCard(@PathVariable Integer orderId,
                                              @AuthenticationPrincipal User user,
                                              @RequestBody @Valid OrderPaymentRequestDTO card) {
        return ResponseEntity.status(200).body(orderService.payOrderWithCard(orderId, user.getId(), card));
    }

    @PostMapping("/{orderId}/payment/sync")
    public ResponseEntity<?> syncOrderPayment(@PathVariable Integer orderId,
                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(orderService.syncOrderPayment(orderId, user.getId()));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.status(200).body(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer orderId,
                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(orderService.getOrderById(orderId, user.getId()));
    }

    @GetMapping("/{orderId}/invoice/pdf")
    public ResponseEntity<?> generateInvoicePdf(@PathVariable Integer orderId,
                                                @AuthenticationPrincipal User user) {
        Orders order = orderRepository.findOrderById(orderId);
        if (order == null) {
            throw new ApiException("Order not found");
        }

        byte[] pdfBytes = orderService.generateInvoicePdf(orderId, user.getId());
        return ResponseEntity.status(200)
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice-" + order.getInvoiceNumber() + ".pdf\"")
                .body(pdfBytes);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.status(200).body(new ApiResponse("Order deleted"));
    }
}
