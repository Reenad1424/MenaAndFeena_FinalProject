package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GeminiService {

    //@Value("${gemini.api.key}")
    private String apiKey;

   // private final RestTemplate restTemplate;

    private static final String GEMINI_URL ="https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    public String moderateAnnouncement(String title, String content) {

        String prompt =
                "You are a content moderation system. " +
                        "Check if this neighborhood announcement is appropriate. " +
                        "Return only APPROVED or REJECTED.\n\n" +
                        "Title: " + title +
                        "\nContent: " + content;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers); // يجمع الbody + headers .... في Request واحد ويرسله إلى Gemini.



        ResponseEntity<String> response = restTemplate.exchange(
                        GEMINI_URL + apiKey,
                        HttpMethod.POST,
                        request,
                        String.class
                );
       // return prompt;
        return response.getBody();

    }






}