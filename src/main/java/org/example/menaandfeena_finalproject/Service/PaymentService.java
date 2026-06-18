package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.PaymentRequestDTO;
import org.example.menaandfeena_finalproject.DTO.Out.PaymentOutDTO;
import org.example.menaandfeena_finalproject.Model.Payment;
import org.example.menaandfeena_finalproject.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${moyasar.api.key}")
    private String apiKey;

    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    private final PaymentRepository paymentRepository;

    public List<PaymentOutDTO> getAllPayments() {
        List<PaymentOutDTO> paymentOutDTOS = new ArrayList<>();

        for (Payment payment : paymentRepository.findAll()) {
            paymentOutDTOS.add(new PaymentOutDTO(payment.getId(), payment.getAmount(), payment.getPlatformFee(), payment.getSellerAmount(), payment.getStatus(), payment.getTransactionId(), payment.getPaymentUrl(), payment.getDescription()));
        }

        return paymentOutDTOS;
    }

    public void deletePayment(Integer id) {
        Payment payment = paymentRepository.findPaymentById(id);

        if (payment == null) {
            throw new ApiException("Payment not found");
        }

        paymentRepository.delete(payment);
    }

    public ResponseEntity<String> processPayment(PaymentRequestDTO paymentRequest) {
        String url = "https://api.moyasar.com/v1/payments";
        String callbackUrl = paymentRequest.getCallbackurl() == null ? "http://localhost:8080/api/v1/payment/callback" : paymentRequest.getCallbackurl();
        String description = paymentRequest.getDescription() == null ? "" : paymentRequest.getDescription();

        String requestBody =
                "source[type]=card"
                        + "&source[name]=" + URLEncoder.encode(paymentRequest.getName(), StandardCharsets.UTF_8)
                        + "&source[number]=" + URLEncoder.encode(paymentRequest.getNumber(), StandardCharsets.UTF_8)
                        + "&source[cvc]=" + URLEncoder.encode(paymentRequest.getCvc(), StandardCharsets.UTF_8)
                        + "&source[month]=" + URLEncoder.encode(paymentRequest.getMonth(), StandardCharsets.UTF_8)
                        + "&source[year]=" + URLEncoder.encode(paymentRequest.getYear(), StandardCharsets.UTF_8)
                        + "&amount=" + paymentRequest.getAmount()
                        + "&currency=" + URLEncoder.encode(paymentRequest.getCurrency(), StandardCharsets.UTF_8)
                        + "&description=" + URLEncoder.encode(description, StandardCharsets.UTF_8)
                        + "&callback_url=" + URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public String getPaymentStatus(String paymentId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(MOYASAR_API_URL + paymentId, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
