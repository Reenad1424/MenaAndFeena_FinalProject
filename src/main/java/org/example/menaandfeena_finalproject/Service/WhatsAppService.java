package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class WhatsAppService {
    @Value("${ultramsg.instance.id}")
    private String instanceId;

    @Value("${ultramsg.token}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendWhatsAppMessage(String to, String message) {
        String url = "https://api.ultramsg.com/" + instanceId + "/messages/chat";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", token);
        body.add("to", to);
        body.add("body", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        System.out.println("WHATSAPP RESPONSE = " + response.getBody());

    }
}
