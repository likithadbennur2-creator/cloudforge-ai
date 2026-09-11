package com.cloudforge.executor;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class InfrastructureExecutor {

    private final InfrastructureExecutionAuthorizationService authorizationService;

    public InfrastructureExecutor(
            InfrastructureExecutionAuthorizationService authorizationService) {

        this.authorizationService = authorizationService;
    }

    public String execute(Map<String, Object> action) {

        Object projectIdObj = action.get("projectId");

        if (projectIdObj == null ||
                projectIdObj.toString().isBlank()) {

            return "Execution blocked: projectId is required";
        }

        String projectId = projectIdObj.toString();

        Map<String, Object> authorization =
                authorizationService.checkAuthorization(projectId);

        boolean authorized =
                Boolean.TRUE.equals(
                        authorization.get("authorized"));

        if (!authorized) {

            return "Execution blocked: project is not authorized";
        }

        return "Execution authorized successfully: " + action;
    }
}