package com.example.loganalyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    public String getFixSuggestion(String errorMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            return "No OpenAI API key provided. Skipping automatic analysis.";
        }

        try {
            RestClient restClient = RestClient.builder().build();

            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "system", "content", "You are an expert software engineer. Review the error log and give a concise 2-sentence explanation and recommended fix."),
                    Map.of("role", "user", "content", "Error log: " + errorMessage)
                ),
                "temperature", 0.2
            );

            Map<String, Object> response = restClient.post()
                    .uri(apiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response != null && response.containsKey("choices")) {
                Object choicesObj = response.get("choices");
                if (choicesObj instanceof List<?> choices && !choices.isEmpty()) {
                    Object firstChoice = choices.get(0);
                    if (firstChoice instanceof Map<?, ?> choiceMap) {
                        Object messageObj = choiceMap.get("message");
                        if (messageObj instanceof Map<?, ?> messageMap) {
                            return String.valueOf(messageMap.get("content"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            return "Analysis unavailable: " + e.getMessage();
        }

        return "No suggestion generated.";
    }
}