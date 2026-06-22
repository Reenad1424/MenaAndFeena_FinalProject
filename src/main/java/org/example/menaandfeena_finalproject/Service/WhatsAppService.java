package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WhatsAppService {

    @Value("${ultramsg.instance.id}")
    private String instanceId;

    @Value("${ultramsg.token}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    // =========================
    // SEND TEXT MESSAGE
    // =========================

    public void sendWhatsAppMessage(
            String to,
            String message
    ) {

        String url =
                "https://api.ultramsg.com/"
                        + instanceId
                        + "/messages/chat";

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("token", token);
        body.add("to", formatSaudiPhone(to));
        body.add("body", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        String.class
                );

        System.out.println(
                "WHATSAPP RESPONSE = "
                        + response.getBody()
        );
    }


    // =========================
    // SEND PDF / DOCUMENT
    // =========================

    public void sendWhatsAppDocument(
            String to,
            String filename,
            String document,
            String caption
    ) {

        String url =
                "https://api.ultramsg.com/"
                        + instanceId
                        + "/messages/document";

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("token", token);
        body.add("to", formatSaudiPhone(to));
        body.add("filename", filename);
        body.add("document", document);
        body.add("caption", caption);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        String.class
                );

        System.out.println(
                "WHATSAPP DOCUMENT RESPONSE = "
                        + response.getBody()
        );
    }


    // =========================
    // FORMAT SAUDI NUMBER
    // =========================

    private String formatSaudiPhone(
            String phone
    ) {

        if (phone == null || phone.isBlank()) {
            throw new ApiException(
                    "رقم الجوال غير موجود"
            );
        }

        String cleaned =
                phone.replace("+", "")
                        .replace(" ", "");

        if (cleaned.startsWith("05")) {
            return "966" + cleaned.substring(1);
        }

        if (cleaned.startsWith("5")) {
            return "966" + cleaned;
        }

        if (cleaned.startsWith("966")) {
            return cleaned;
        }

        throw new ApiException(
                "رقم الجوال غير صالح"
        );
    }
}