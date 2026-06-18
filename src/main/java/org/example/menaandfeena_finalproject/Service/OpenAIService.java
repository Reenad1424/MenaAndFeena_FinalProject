package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

//@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // دالة عامة تستقبل التوجيه (System Prompt) والنص المراد معالجته (User Content)
    public String askAI(String systemPrompt, String userContent) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        // تجهيز جسم الطلب (Request Body)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");

        List<Map<String, String>> messages = new ArrayList<>();

        // 1. تحديد دور وتوجيه الذكاء الاصطناعي
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        // 2. تمرير النص المرسل من المستخدم أو النظام
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userContent);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        // إعدادات الحماية والـ Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            List<Map> choices = (List<Map>) response.getBody().get("choices");
            Map messageResult = (Map) choices.get(0).get("message");
            return messageResult.get("content").toString();

        } catch (Exception ex) {
            System.out.println("❌ [OPENAI EXCEPTION]: " + ex.getMessage());
            return "ERROR_FALLBACK"; // نص بديل وموحد في حال فشل الاتصال بالـ API
        }
    }
}
