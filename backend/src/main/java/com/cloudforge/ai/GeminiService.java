package com.cloudforge.ai;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String analyzeProblem(String problem) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
                + apiKey;

        String prompt = """
                You are CloudForge AI, a cloud infrastructure planning assistant.

                Analyze the user's infrastructure problem.

                User problem:
                %s

                Return ONLY valid JSON.
                Do not use markdown.
                Do not use ```json.
                Do not provide explanations outside the JSON.

                The JSON must have exactly these fields:

                {
                  "problem": "short description of the problem",
                  "recommendation": "recommended infrastructure strategy",
                  "instances": 1,
                  "loadBalancer": false,
                  "cdn": false,
                  "priority": "LOW"
                }

                Rules:

                - instances must be a whole number.
                - loadBalancer must be true or false.
                - cdn must be true or false.
                - priority must be LOW, MEDIUM, or HIGH.
                - Do NOT execute commands.
                - Do NOT provide shell commands.
                - Do NOT create Terraform code.
                - Only create an infrastructure recommendation.
                """.formatted(problem);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", new Object[]{textPart});

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("responseMimeType", "application/json");

        Map<String, Object> request = new HashMap<>();
        request.put("contents", new Object[]{content});
        request.put("generationConfig", generationConfig);

        Map<String, Object> response = restTemplate.postForObject(
                url,
                request,
                Map.class
        );

        if (response == null) {
            throw new RuntimeException("Empty response from Gemini");
        }

        try {
            Map<String, Object> candidate =
                    (Map<String, Object>) ((java.util.List<?>) response.get("candidates")).get(0);

            Map<String, Object> responseContent =
                    (Map<String, Object>) candidate.get("content");

            java.util.List<?> parts =
                    (java.util.List<?>) responseContent.get("parts");

            Map<String, Object> part =
                    (Map<String, Object>) parts.get(0);

            return (String) part.get("text");

        } catch (Exception e) {
            throw new RuntimeException("Could not parse Gemini response", e);
        }
    }
}