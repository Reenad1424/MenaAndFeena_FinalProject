package org.example.menaandfeena_finalproject.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiResponse;
import org.example.menaandfeena_finalproject.DTO.In.OrderPaymentRequestDTO;
import org.example.menaandfeena_finalproject.DTO.In.PaymentRequestDTO;
import org.example.menaandfeena_finalproject.DTO.Out.PaymentInvoiceDTO;
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

    @PostMapping("/card")
    public ResponseEntity<?> processPayment(@RequestBody @Valid PaymentRequestDTO paymentRequest) {
        return ResponseEntity.status(200).body(paymentService.processPayment(paymentRequest).getBody());
    }

    @GetMapping("/get-status/{id}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String id) {

        return ResponseEntity.status(200)
                .body(paymentService.getPaymentStatus(id));
    }

//    @GetMapping("/invoice/{id}")
//    public ResponseEntity<?> getPaymentInvoice(@PathVariable String id) {
//
//        return ResponseEntity.status(200).body(paymentService.getPaymentInvoice(id));
//    }


// original
//    @GetMapping("/get-status/{id}")
//    public ResponseEntity<?> getPaymentStatus(@PathVariable String id) {
//
//        return ResponseEntity.status(200)
//                .body(paymentService.getPaymentStatus(id));
//    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.status(200).body(paymentService.getAllPayments());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Integer id) {
        paymentService.deletePayment(id);
        return ResponseEntity.status(200).body(new ApiResponse("Payment deleted"));
    }



 // Walaa
//    @PostMapping("/pay-event/{registrationId}")
//    public ResponseEntity<?> payEventRegistration(@PathVariable Integer registrationId,
//                                               @RequestBody @Valid OrderPaymentRequestDTO card) {
//
//        return ResponseEntity.status(200).body(paymentService.payEventRegistration(registrationId, card));
//    }


    @PostMapping("/pay-event/{registrationId}")
    public ResponseEntity<?> payEventRegistration(Authentication authentication,
                                                  @PathVariable Integer registrationId,
                                                  @RequestBody @Valid OrderPaymentRequestDTO card) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.status(200)
                .body(paymentService.payEventRegistration(user.getId(), registrationId, card));
    }



// Walaa

    @GetMapping("/callback")
    public ResponseEntity<?> paymentCallback(@RequestParam String id,
                                          @RequestParam String status,
                                          @RequestParam(required = false) String message) {

        paymentService.handlePaymentCallback(id, status);
        return ResponseEntity.status(200).body(new ApiResponse("Payment callback handled successfully"));
    }



   // Walaa
    @GetMapping("/invoice-pdf/{paymentId}")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable String paymentId) throws Exception {
        byte[] pdf = pdfInvoiceService.generateInvoice(paymentId);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "inline; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }



}
