package org.example.menaandfeena_finalproject.Controller;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.PaymentInDTO;
import org.example.menaandfeena_finalproject.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.status(200).body(paymentService.getAllPayments());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPayment(@RequestBody PaymentInDTO paymentInDTO) {
        paymentService.addPayment(paymentInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Payment added"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Integer id, @RequestBody PaymentInDTO paymentInDTO) {
        paymentService.updatePayment(id, paymentInDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Payment updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Integer id) {
        paymentService.deletePayment(id);
        return ResponseEntity.status(200).body(new ApiResponse("Payment deleted"));
    }
}
