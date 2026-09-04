package com.cloudforge.executor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InfrastructureCommandService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CommandGeneratorService commandGeneratorService;

    public InfrastructureCommandService(
            CommandGeneratorService commandGeneratorService) {

        this.commandGeneratorService = commandGeneratorService;
    }

    public Map<String, Object> generateCommands(String projectId) {

        try {
            // Read the existing requirements.json
            File requirementsFile =
                    new File("generated/requirements.json");

            if (!requirementsFile.exists()) {
                throw new RuntimeException(
                        "generated/requirements.json not found"
                );
            }

            JsonNode root =
                    objectMapper.readTree(requirementsFile);

            JsonNode requirements =
                    root.path("requirements");

            List<String> generatedCommands =
                    new ArrayList<>();

            // -------------------------
            // COMPUTE
            // -------------------------

            JsonNode compute =
                    requirements.path("compute");

            if (compute.path("required").asBoolean(false)) {

                int instances =
                        compute.path("minimum_instances").asInt(1);

                String region =
        requirements.path("region").asText();

if (region == null || region.isBlank()) {
    region = "asia-south1";
}

                Map<String, String> variables =
                        new HashMap<>();

                variables.put("PROJECT_ID", projectId);
                variables.put("REGION", region);
                variables.put(
                        "INSTANCE_COUNT",
                        String.valueOf(instances)
                );

                String command =
                        commandGeneratorService.generateCommand(
                                "command-templates/compute/scale-compute.sh",
                                variables
                        );

                generatedCommands.add(command);
            }

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put("status", "SUCCESS");
            response.put("projectId", projectId);
            response.put("commands", generatedCommands);

            return response;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate infrastructure commands",
                    e
            );
        }
    }
}