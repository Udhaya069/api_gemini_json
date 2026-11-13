package com.example.ai_text_api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class GeminiService {

    private static final String SYSTEM_PROMPT =
            "You are a helpful assistant. Keep answers short and clear.";

    private final RestClient restClient;
    private final String model;

    public GeminiService(@Value("${gemini.api-key}") String apiKey,
                         @Value("${gemini.model}") String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
    }

    public String generate(String userText) {
        Map<String, Object> body = Map.of(
                "systemInstruction",
                Map.of("parts", List.of(Map.of("text", SYSTEM_PROMPT))),
                "contents",
                List.of(Map.of("parts", List.of(Map.of("text", userText)))));

        GeminiResponse response = restClient.post()
                .uri("/models/{model}:generateContent", model)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(GeminiResponse.class);

        return response.candidates().get(0).content().parts().get(0).text();
    }
    public String generate(String userText) {
    int maxAttempts = 3;
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
        try {
            return callGemini(userText);
        } catch (HttpServerErrorException | HttpClientErrorException.TooManyRequests e) {
            if (attempt == maxAttempts) {
                throw e;
            }
            try {
                Thread.sleep(1000L * attempt); // wait 1s, then 2s
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }
    }
    throw new IllegalStateException("Unreachable");
}

    // These records match the JSON path: candidates[0].content.parts[0].text
    record GeminiResponse(List<Candidate> candidates) {
        record Candidate(Content content) {}
        record Content(List<Part> parts) {}
        record Part(String text) {}
    }
}