package com.cloudforge.executor;

import java.io.InputStream;
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
            // -----------------------------------------
            // 1. Read requirements.json
            // -----------------------------------------

            java.io.File requirementsFile =
                    new java.io.File("generated/requirements.json");

            if (!requirementsFile.exists()) {
                throw new RuntimeException(
                        "generated/requirements.json not found"
                );
            }

            JsonNode root =
                    objectMapper.readTree(requirementsFile);

            JsonNode requirements =
                    root.path("requirements");


            // -----------------------------------------
            // 2. Read actions.json
            // -----------------------------------------

            InputStream inputStream =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream("actions.json");

            if (inputStream == null) {
                throw new RuntimeException(
                        "actions.json not found"
                );
            }

            JsonNode actionsRoot =
                    objectMapper.readTree(inputStream);

            JsonNode actionDefinitions =
                    actionsRoot.path("actions");


            // -----------------------------------------
            // 3. Prepare generated commands
            // -----------------------------------------

            List<String> generatedCommands =
                    new ArrayList<>();


            // -----------------------------------------
            // 4. Check compute requirement
            // -----------------------------------------

            JsonNode compute =
                    requirements.path("compute");

            if (compute.path("required").asBoolean(false)) {

                int instances =
                        compute.path("minimum_instances")
                                .asInt(1);

                String region =
                        requirements.path("region")
                                .asText();

                if (region == null || region.isBlank()) {
                    region = "asia-south1";
                }


                // -----------------------------------------
                // Find compute action in actions.json
                // -----------------------------------------

                for (JsonNode action : actionDefinitions) {

                    if ("scale_compute".equals(
                            action.path("id").asText())) {

                        String templatePath =
                                action.path("commandTemplate")
                                        .asText();

                        if (templatePath == null ||
                                templatePath.isBlank()) {

                            throw new RuntimeException(
                                    "commandTemplate missing for scale_compute"
                            );
                        }


                        // -----------------------------------------
                        // Prepare template variables
                        // -----------------------------------------

                        Map<String, String> variables =
                                new HashMap<>();

                        variables.put(
                                "PROJECT_ID",
                                projectId
                        );

                        variables.put(
                                "REGION",
                                region
                        );

                        variables.put(
                                "INSTANCE_COUNT",
                                String.valueOf(instances)
                        );


                        // -----------------------------------------
                        // Generate command
                        // -----------------------------------------

                        String command =
                                commandGeneratorService.generateCommand(
                                        templatePath,
                                        variables
                                );

                        generatedCommands.add(command);

                        break;
                    }
                }
            }


            // -----------------------------------------
            // 5. Build response
            // -----------------------------------------

            Map<String, Object> response =
                    new LinkedHashMap<>();

            response.put(
                    "status",
                    "SUCCESS"
            );

            response.put(
                    "projectId",
                    projectId
            );

            response.put(
                    "commands",
                    generatedCommands
            );

            return response;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate infrastructure commands",
                    e
            );
        }
    }
}