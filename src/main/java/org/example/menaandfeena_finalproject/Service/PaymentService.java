package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.OrderPaymentRequestDTO;
import org.example.menaandfeena_finalproject.DTO.In.PaymentRequestDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MoyasarChargeOutDTO;
import org.example.menaandfeena_finalproject.DTO.Out.PaymentInvoiceDTO;
import org.example.menaandfeena_finalproject.DTO.Out.PaymentOutDTO;
import org.example.menaandfeena_finalproject.Model.Event;
import org.example.menaandfeena_finalproject.Model.EventRegistration;
import org.example.menaandfeena_finalproject.Model.Payment;
import org.example.menaandfeena_finalproject.Repository.EventRegistrationRepository;
import org.example.menaandfeena_finalproject.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final EventRegistrationRepository eventRegistrationRepository;
    @Value("${moyasar.api.key}")

    private String apiKey;

    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    private final PaymentRepository paymentRepository;

    // يرجع كل عمليات الدفع المحفوظة عندنا في قاعدة البيانات، وليس من Moyasar.
    public List<PaymentOutDTO> getAllPayments() {
        List<PaymentOutDTO> paymentOutDTOS = new ArrayList<>();

        for (Payment payment : paymentRepository.findAll()) {
            paymentOutDTOS.add(new PaymentOutDTO(payment.getId(), payment.getAmount(), payment.getPlatformFee(), payment.getSellerAmount(), payment.getStatus(), payment.getTransactionId(), payment.getPaymentUrl(), payment.getDescription()));
        }

        return paymentOutDTOS;
    }

    // يحذف عملية دفع محلية من قاعدة البيانات.
    public void deletePayment(Integer id) {
        Payment payment = paymentRepository.findPaymentById(id);

        if (payment == null) {
            throw new ApiException("Payment not found");
        }

        paymentRepository.delete(payment);
    }

    // هذا ميثود عام لتجربة الدفع مع Moyasar مباشرة.
    // يرسل بيانات البطاقة إلى Moyasar ويرجع الرد الخام كما هو.
    public ResponseEntity<String> processPayment(PaymentRequestDTO paymentRequest) {
        String url = "https://api.moyasar.com/v1/payments";
        String callbackUrl = paymentRequest.getCallbackurl() == null ? "http://localhost:8080/api/v1/payment/callback" : paymentRequest.getCallbackurl();
        String description = paymentRequest.getDescription() == null ? "" : paymentRequest.getDescription();

        // Moyasar يحتاج بيانات البطاقة بصيغة form-urlencoded.
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



    // هذا فقط Wrapper واضح، يرجع بيانات الدفع بعد قراءتها بدل JSON الخام.
    public MoyasarChargeOutDTO getPaymentStatusDetails(String paymentId) {
        return fetchPayment(paymentId);
    }

    // يجلب عملية دفع واحدة من Moyasar باستخدام رقم الدفع الخاص بـ Moyasar.
    // مهم: هذا الرقم يكون محفوظ عندنا في Payment.transactionId وليس Payment.id.
    public MoyasarChargeOutDTO fetchPayment(String moyasarPaymentId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(MOYASAR_API_URL + moyasarPaymentId, HttpMethod.GET, entity, String.class);
            JsonNode node = new ObjectMapper().readTree(response.getBody());
            // نأخذ فقط الحقول التي نحتاجها في مزامنة دفع الطلب.
            String id = node.path("id").asText(null);
            String status = node.path("status").asText(null);
            Integer amount = node.path("amount").isMissingNode() || node.path("amount").isNull() ? null : node.path("amount").asInt();
            String transactionUrl = node.path("source").path("transaction_url").asText(null);
            return new MoyasarChargeOutDTO(id, status, amount, transactionUrl);
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiException("Moyasar authentication failed while fetching payment " + moyasarPaymentId + ": " + responseBody);
            }
            throw new ApiException("Moyasar rejected payment status request for " + moyasarPaymentId + ": " + e.getStatusCode() + " " + responseBody);
        } catch (Exception e) {
            throw new ApiException("Could not get Moyasar payment status for " + moyasarPaymentId + ": " + e.getMessage());
        }
    }

    // ينفذ الدفع بالبطاقة عن طريق Moyasar لصفحة الدفع داخل التطبيق.
    // amount لازم يكون بالهللات، ويرجع رقم دفع Moyasar والحالة ورابط 3DS إذا وجد.
    public MoyasarChargeOutDTO chargeCard(Integer amount, String currency, OrderPaymentRequestDTO card, String description) {
        String url = "https://api.moyasar.com/v1/payments";
       // String callbackUrl = "http://localhost:8080/api/v1/payment/callback";
        String callbackUrl = "http://localhost:8089/api/v1/payment/callback";

        // هذا طلب الدفع الحقيقي الخاص بالسوق، والمبلغ يتم تجهيزه في OrderService.
        String requestBody =
                "source[type]=card"
                        + "&source[name]=" + URLEncoder.encode(card.getName(), StandardCharsets.UTF_8)
                        + "&source[number]=" + URLEncoder.encode(card.getNumber(), StandardCharsets.UTF_8)
                        + "&source[cvc]=" + URLEncoder.encode(card.getCvc(), StandardCharsets.UTF_8)
                        + "&source[month]=" + URLEncoder.encode(card.getMonth(), StandardCharsets.UTF_8)
                        + "&source[year]=" + URLEncoder.encode(card.getYear(), StandardCharsets.UTF_8)
                        + "&amount=" + amount
                        + "&currency=" + URLEncoder.encode(currency, StandardCharsets.UTF_8)
                        + "&description=" + URLEncoder.encode(description == null ? "" : description, StandardCharsets.UTF_8)
                        + "&callback_url=" + URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode node = new ObjectMapper().readTree(response.getBody());
            // إذا رجع Moyasar الحالة initiated، فهذا الرابط هو صفحة 3DS التي يفتحها المستخدم.
            String id = node.path("id").asText(null);
            String status = node.path("status").asText(null);
            Integer responseAmount = node.path("amount").isMissingNode() || node.path("amount").isNull() ? null : node.path("amount").asInt();
            String transactionUrl = node.path("source").path("transaction_url").asText(null);
            return new MoyasarChargeOutDTO(id, status, responseAmount, transactionUrl);
        } catch (Exception e) {
            throw new ApiException("Could not process Moyasar card payment: " + e.getMessage());
        }
    }

    // Walaa

    public MoyasarChargeOutDTO payEventRegistration(Integer registrationId, OrderPaymentRequestDTO card) {
        EventRegistration registration = eventRegistrationRepository.findEventRegistrationById(registrationId);

        if (registration == null) {
            throw new ApiException("Event registration not found");
        }

        if (!registration.getStatus().equals("PENDING")) {
            throw new ApiException("Only pending registrations can be paid");
        }

        Event event = registration.getEvent();

        if (!event.getIsPaid()) {
            throw new ApiException("This event is free and does not require payment");
        }

        Integer amountInHalalas = (int) (event.getPrice() * 100);

        MoyasarChargeOutDTO result = chargeCard(
                amountInHalalas,
                "SAR",
                card,
                "Payment for event: " + event.getTitle()
        );

        Payment payment = new Payment();
        payment.setAmount(amountInHalalas);
        payment.setPlatformFee(0);
        payment.setSellerAmount(amountInHalalas);
        payment.setStatus(result.getStatus().equalsIgnoreCase("paid") ? "PAID" : "PENDING");
        payment.setTransactionId(result.getMoyasarPaymentId());
        payment.setPaymentUrl(result.getTransactionUrl());

                payment.setDescription("Payment for event: " + event.getTitle());
        payment.setEventRegistration(registration);

        paymentRepository.save(payment);

        if (result.getStatus().equalsIgnoreCase("paid")) {
            registration.setStatus("CONFIRMED");
            eventRegistrationRepository.save(registration);
        }

        return result;
    }



// Walaa
    public void handlePaymentCallback(String paymentId, String status) {

        Payment payment = paymentRepository.findPaymentByTransactionId(paymentId);

        if (payment == null) {
            throw new ApiException("Payment not found");
        }

        if (status.equalsIgnoreCase("paid")) {

            payment.setStatus("PAID");
            paymentRepository.save(payment);

            EventRegistration registration = payment.getEventRegistration();

            if (registration != null) {
                registration.setStatus("CONFIRMED");
                eventRegistrationRepository.save(registration);
            }

        } else {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
        }
    }

    //Walaa
//    public PaymentInvoiceDTO getPaymentInvoice(String paymentId) {
//        MoyasarChargeOutDTO payment = fetchPayment(paymentId);
//
//        return new PaymentInvoiceDTO(
//                payment.getMoyasarPaymentId(),
//                payment.getStatus(),
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//    }


// Walaa
    public PaymentInvoiceDTO getPaymentInvoice(String paymentId) {
        String rawJson = getPaymentStatus(paymentId);

        try {
            JsonNode node = new ObjectMapper().readTree(rawJson);
            Payment payment = paymentRepository.findPaymentByTransactionId(paymentId);

            String eventTitle = null;

            if (payment != null && payment.getEventRegistration() != null) {
                eventTitle = payment.getEventRegistration().getEvent().getTitle();
            }

            return new PaymentInvoiceDTO(
                    node.path("id").asText(),
                    node.path("status").asText(),
                    node.path("source").path("message").asText(),
                    node.path("description").asText(),

                    eventTitle, // eventTitle

                    node.path("amount_format").asText(),
                    node.path("currency").asText(),
                    node.path("fee_format").asText(),
                    node.path("refunded_format").asText(),
                    node.path("captured_format").asText(),

                    node.path("source").path("company").asText(),
                    node.path("source").path("name").asText(),
                    node.path("source").path("issuer_name").asText(),
                    node.path("source").path("issuer_country").asText(),

                    node.path("created_at").asText()
            );
        } catch (Exception e) {
            throw new ApiException("Could not build payment invoice: " + e.getMessage());
        }
    }








}
