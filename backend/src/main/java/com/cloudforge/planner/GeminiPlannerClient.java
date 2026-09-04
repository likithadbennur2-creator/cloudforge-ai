package com.cloudforge.planner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GeminiPlannerClient {

    @Value("${gemini.planner.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public String generatePlan(String problem) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
                        + apiKey;

        String prompt = """
                You are CloudForge Infrastructure Requirements Analyzer.

                Analyze the following cloud infrastructure problem.

                Return ONLY valid JSON.
                Do NOT use markdown.
                Do NOT use ```json.
                Do NOT add explanations.

                Required JSON structure:

                {
                  "requirements": {
                    "application_type": "",
                    "traffic_level": "",
                    "daily_users": 0,
                    "availability": "",
                    "auto_scaling": false,
                    "load_balancer": false,
                    "cdn": false,
                    "compute": {
                      "required": false,
                      "minimum_instances": 0
                    },
                    "region": "",
                    "database": {
                      "required": false,
                      "type": ""
                    }
                  }
                }

                Rules:

                application_type:
                web_application, mobile_backend, api, microservice, or other

                traffic_level:
                low, medium, or high

                daily_users:
                number if mentioned, otherwise 0

                availability:
                low, medium, or high

                auto_scaling:
                true only when scaling is required

                load_balancer:
                true when high availability or traffic distribution requires it

                cdn:
                true when static content or global/high traffic delivery benefits from CDN

                compute.required:
                true when compute infrastructure is required

                compute.minimum_instances:
                recommended minimum number of instances

                region:
                recommended Google Cloud region, or empty string if insufficient information

                database.required:
                true only when persistent database storage is required

                database.type:
                postgresql, mysql, mongodb, or empty string

                Problem:
                %s
                """.formatted(problem);

        try {

            String requestBody = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {
                              "text": %s
                            }
                          ]
                        }
                      ],
                      "generationConfig": {
                        "temperature": 0.1,
                        "responseMimeType": "application/json"
                      }
                    }
                    """.formatted(
                    objectMapper.writeValueAsString(prompt)
            );

            String response = restTemplate.postForObject(
                    url,
                    requestBody,
                    String.class
            );

            // Parse Gemini response
            JsonNode root = objectMapper.readTree(response);

            String jsonText = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // Validate that Gemini actually returned JSON
            JsonNode requirementsJson =
                    objectMapper.readTree(jsonText);
                    
                    Path outputPath = Paths.get("generated/requirements.json");

Files.createDirectories(outputPath.getParent());

objectMapper
        .writerWithDefaultPrettyPrinter()
        .writeValue(outputPath.toFile(), requirementsJson);

            // Return clean JSON only
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(requirementsJson);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate infrastructure requirements",
                    e
            );
        }
    }
}