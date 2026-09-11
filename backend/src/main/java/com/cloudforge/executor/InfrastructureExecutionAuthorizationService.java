package com.cloudforge.executor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class InfrastructureExecutionAuthorizationService {

    private final Set<String> approvedProjects = new HashSet<>();

    public Map<String, Object> approve(String projectId) {

        approvedProjects.add(projectId);

        return Map.of(
                "projectId", projectId,
                "approved", true,
                "status", "APPROVED"
        );
    }

    public Map<String, Object> checkAuthorization(String projectId) {

        boolean approved =
                approvedProjects.contains(projectId);

        return Map.of(
                "projectId", projectId,
                "authorized", approved,
                "status", approved ? "AUTHORIZED" : "BLOCKED"
        );
    }
}