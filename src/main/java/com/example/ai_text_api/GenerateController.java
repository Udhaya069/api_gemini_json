package com.example.ai_text_api;

import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class GenerateController {

    private final GeminiService geminiService;
private static final Logger log = LoggerFactory.getLogger(GenerateController.class);
    public GenerateController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/generate")
    public GenerateResponse generate(@RequestBody GenerateRequest request) {
        String answer = geminiService.generate(request.text());
        return new GenerateResponse(answer);
    }
   @ExceptionHandler(RestClientResponseException.class)
public ResponseEntity<Map<String, String>> handleAiError(RestClientResponseException e) {
    log.error("Gemini error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(Map.of("error", "The AI service is busy or unavailable. Please try again."));
}

    record GenerateRequest(String text) {}
    record GenerateResponse(String answer) {}
}