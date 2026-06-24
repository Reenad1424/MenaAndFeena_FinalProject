package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.OrderPaymentRequestDTO;
import org.example.menaandfeena_finalproject.DTO.In.PaymentRequestDTO;
import org.example.menaandfeena_finalproject.DTO.Out.PaymentInvoiceDTO;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.Payment;
import org.example.menaandfeena_finalproject.Repository.PaymentRepository;
import org.example.menaandfeena_finalproject.Service.OrderService;
import org.example.menaandfeena_finalproject.Service.PaymentService;
import org.example.menaandfeena_finalproject.Service.PdfInvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.example.menaandfeena_finalproject.Model.User;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PdfInvoiceService pdfInvoiceService;
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @PostMapping("/card")
    public ResponseEntity<?> processPayment(@RequestBody @Valid PaymentRequestDTO paymentRequest) {
        return ResponseEntity.status(200).body(paymentService.processPayment(paymentRequest).getBody());
    }

    @GetMapping("/get-status/{id}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String id) {

        return ResponseEntity.status(200)
                .body(paymentService.getPaymentStatus(id));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.status(200).body(paymentService.getAllPayments());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Integer id) {
        paymentService.deletePayment(id);
        return ResponseEntity.status(200).body(new ApiResponse("Payment deleted"));
    }

    @PostMapping("/pay-event/{registrationId}")
    public ResponseEntity<?> payEventRegistration(Authentication authentication,
                                                  @PathVariable Integer registrationId,
                                                  @RequestBody @Valid OrderPaymentRequestDTO card) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.status(200)
                .body(paymentService.payEventRegistration(user.getId(), registrationId, card));
    }

    @GetMapping("/callback")
    public ResponseEntity<?> paymentCallback(@RequestParam String id,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) String message) {

        Payment payment = paymentRepository.findPaymentByTransactionId(id);
        if (payment == null) {
            throw new ApiException("Payment not found");
        }

        Object callbackResult;
        if (payment.getOrders() != null) {
            callbackResult = orderService.handlePaymentCallback(payment);
        } else {
            paymentService.handlePaymentCallback(id);
            callbackResult = new ApiResponse("Payment callback handled successfully");
        }

        return ResponseEntity.status(200).body(callbackResult);
    }



    // Walaa
    @GetMapping("/invoice-pdf/{paymentId}")
    public ResponseEntity<byte[]> generateInvoicePdf(Authentication authentication,
                                                     @PathVariable String paymentId) throws Exception {
        User user = (User) authentication.getPrincipal();
        byte[] pdf = pdfInvoiceService.generateInvoiceForUser(paymentId, user.getId());
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "inline; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }



}
