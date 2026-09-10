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
            // 4. Region
            // -----------------------------------------

            String region =
                    requirements.path("region").asText();

            if (region == null || region.isBlank()) {
                region = "asia-south1";
            }


            // -----------------------------------------
            // 5. Common variables
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


            // -----------------------------------------
            // 6. Compute requirement
            // -----------------------------------------

            JsonNode compute =
                    requirements.path("compute");

            if (compute.path("required").asBoolean(false)) {

                int instances =
                        compute.path("minimum_instances")
                                .asInt(1);

                variables.put(
                        "INSTANCE_COUNT",
                        String.valueOf(instances)
                );

                String command =
                        generateActionCommand(
                                actionDefinitions,
                                "scale_compute",
                                variables
                        );

                generatedCommands.add(command);
            }


            // -----------------------------------------
            // 7. Load balancer requirement
            // -----------------------------------------

            boolean loadBalancerRequired =
                    requirements.path("load_balancer")
                            .asBoolean(false);

            if (loadBalancerRequired) {

                String command =
                        generateActionCommand(
                                actionDefinitions,
                                "load_balancer",
                                variables
                        );

                generatedCommands.add(command);
            }


            // -----------------------------------------
            // 8. CDN requirement
            // -----------------------------------------

            boolean cdnRequired =
                    requirements.path("cdn")
                            .asBoolean(false);

            if (cdnRequired) {

                String command =
                        generateActionCommand(
                                actionDefinitions,
                                "cdn",
                                variables
                        );

                generatedCommands.add(command);
            }


            // -----------------------------------------
            // 9. Build response
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
                    "region",
                    region
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


    // =================================================
    // Generate command from actions.json
    // =================================================

    private String generateActionCommand(
            JsonNode actionDefinitions,
            String actionId,
            Map<String, String> variables) {

        for (JsonNode action : actionDefinitions) {

            if (actionId.equals(
                    action.path("id").asText())) {

                String templatePath =
                        action.path("commandTemplate")
                                .asText();

                if (templatePath == null ||
                        templatePath.isBlank()) {

                    throw new RuntimeException(
                            "commandTemplate missing for action: "
                                    + actionId
                    );
                }

                return commandGeneratorService.generateCommand(
                        templatePath,
                        variables
                );
            }
        }

        throw new RuntimeException(
                "Action not found in actions.json: "
                        + actionId
        );
    }
}