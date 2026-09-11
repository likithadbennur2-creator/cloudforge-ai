package com.cloudforge.executor;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class InfrastructureApprovalService {

    private final InfrastructureExecutionAuthorizationService authorizationService;

    // ADD THE CONSTRUCTOR HERE
    public InfrastructureApprovalService(
            InfrastructureExecutionAuthorizationService authorizationService) {

        this.authorizationService = authorizationService;
    }

    public Map<String, Object> approve(
            Map<String, Object> request) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        Object projectIdObj =
                request.get("projectId");

        if (projectIdObj == null ||
                projectIdObj.toString().isBlank()) {

            result.put("approved", false);
            result.put("status", "INVALID");
            result.put("message", "projectId is required");

            return result;
        }

        String projectId =
                projectIdObj.toString();

        // Record approval
        authorizationService.approve(projectId);

        result.put("projectId", projectId);
        result.put("approved", true);
        result.put("status", "APPROVED");
        result.put(
                "message",
                "Infrastructure plan approved. "
                        + "Execution can proceed."
        );

        return result;
    }
}