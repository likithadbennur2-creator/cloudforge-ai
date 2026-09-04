package com.cloudforge.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIAnalysisResponse analyzeProblem(String problem) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
                        + apiKey;

        String prompt = """
                You are CloudForge AI, a cloud infrastructure assistant.

                Analyze the user's infrastructure problem.

                User problem:
                %s

                Return ONLY valid JSON.

                Use exactly this structure:

                {
                  "problem": "short description",
                  "recommendation": "what CloudForge should do",
                  "instances": 1,
                  "loadBalancer": false,
                  "cdn": false,
                  "priority": "LOW"
                }

                Rules:
                - instances must be a number
                - loadBalancer must be true or false
                - cdn must be true or false
                - priority must be LOW, MEDIUM, or HIGH
                - Do not include markdown
                - Do not include ```json
                - Do not provide shell commands
                - Do not execute anything
                """.formatted(problem);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", new Object[]{textPart});

        Map<String, Object> request = new HashMap<>();
        request.put("contents", new Object[]{content});

        String rawResponse = restTemplate.postForObject(
                url,
                request,
                String.class
        );

        try {

            JsonNode root = objectMapper.readTree(rawResponse);

            String text = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            text = text
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return objectMapper.readValue(
                    text,
                    AIAnalysisResponse.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse Gemini response: " + rawResponse,
                    e
            );
        }
    }
}