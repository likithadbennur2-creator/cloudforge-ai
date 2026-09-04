package com.cloudforge.planner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InfrastructurePlannerService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeminiPlannerClient geminiPlannerClient;

    public InfrastructurePlannerService(
            GeminiPlannerClient geminiPlannerClient) {
        this.geminiPlannerClient = geminiPlannerClient;
    }

    public List<Map<String, Object>> createPlan(
            Map<String, Object> requirementsResponse) {

        List<Map<String, Object>> actions = new ArrayList<>();

        try {

            /*
             * Load actions.json
             */
            InputStream inputStream =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream("actions.json");

            if (inputStream == null) {
                throw new RuntimeException(
                        "actions.json not found"
                );
            }

            JsonNode root =
                    objectMapper.readTree(inputStream);

            JsonNode actionDefinitions =
                    root.path("actions");

            /*
             * API 2 returns:
             *
             * {
             *   "requirements": {
             *       ...
             *   }
             * }
             */

            JsonNode requirements =
                    objectMapper.valueToTree(
                            requirementsResponse
                    ).path("requirements");

            if (requirements.isMissingNode()) {
                throw new RuntimeException(
                        "requirements object not found"
                );
            }

            /*
             * Read requirements
             */

            boolean computeRequired =
                    requirements
                            .path("compute")
                            .path("required")
                            .asBoolean(false);

            int minimumInstances =
                    requirements
                            .path("compute")
                            .path("minimum_instances")
                            .asInt(0);

            boolean autoScaling =
                    requirements
                            .path("auto_scaling")
                            .asBoolean(false);

            boolean loadBalancer =
                    requirements
                            .path("load_balancer")
                            .asBoolean(false);

            boolean cdn =
                    requirements
                            .path("cdn")
                            .asBoolean(false);

            String region =
                    requirements
                            .path("region")
                            .asText("");

            boolean databaseRequired =
                    requirements
                            .path("database")
                            .path("required")
                            .asBoolean(false);

            String databaseType =
                    requirements
                            .path("database")
                            .path("type")
                            .asText("");

            /*
             * ------------------------------------------------
             * COMPUTE
             * ------------------------------------------------
             */

            if (computeRequired) {

                for (JsonNode action : actionDefinitions) {

                    if ("scale_compute".equals(
                            action.path("id").asText())) {

                        Map<String, Object> planAction =
                                objectMapper.convertValue(
                                        action,
                                        Map.class
                                );

                        planAction.put(
                                "instances",
                                minimumInstances
                        );

                        planAction.put(
                                "autoScaling",
                                autoScaling
                        );

                        if (!region.isBlank()) {
                            planAction.put(
                                    "region",
                                    region
                            );
                        }

                        actions.add(planAction);
                    }
                }
            }

            /*
             * ------------------------------------------------
             * LOAD BALANCER
             * ------------------------------------------------
             */

            if (loadBalancer) {

                for (JsonNode action : actionDefinitions) {

                    if ("load_balancer".equals(
                            action.path("id").asText())) {

                        Map<String, Object> planAction =
                                objectMapper.convertValue(
                                        action,
                                        Map.class
                                );

                        if (!region.isBlank()) {
                            planAction.put(
                                    "region",
                                    region
                            );
                        }

                        actions.add(planAction);
                    }
                }
            }

            /*
             * ------------------------------------------------
             * CDN
             * ------------------------------------------------
             */

            if (cdn) {

                for (JsonNode action : actionDefinitions) {

                    if ("cdn".equals(
                            action.path("id").asText())) {

                        Map<String, Object> planAction =
                                objectMapper.convertValue(
                                        action,
                                        Map.class
                                );

                        if (!region.isBlank()) {
                            planAction.put(
                                    "region",
                                    region
                            );
                        }

                        actions.add(planAction);
                    }
                }
            }

            /*
             * ------------------------------------------------
             * DATABASE
             * ------------------------------------------------
             */

            if (databaseRequired) {

                for (JsonNode action : actionDefinitions) {

                    if ("database".equals(
                            action.path("id").asText())) {

                        Map<String, Object> planAction =
                                objectMapper.convertValue(
                                        action,
                                        Map.class
                                );

                        planAction.put(
                                "databaseType",
                                databaseType
                        );

                        if (!region.isBlank()) {
                            planAction.put(
                                    "region",
                                    region
                            );
                        }

                        actions.add(planAction);
                    }
                }
            }

            return actions;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create infrastructure plan",
                    e
            );
        }
    }

    /*
     * API 2:
     * Problem → Gemini → requirements.json
     */
    public String createInfrastructurePlan(String problem) {

        return geminiPlannerClient.generatePlan(problem);
    }
}