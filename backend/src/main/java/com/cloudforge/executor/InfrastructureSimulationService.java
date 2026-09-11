package com.cloudforge.executor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class InfrastructureSimulationService {

    public Map<String, Object> simulate(
            Map<String, Object> request) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        List<String> plannedChanges =
                new ArrayList<>();

        // 1. Project
        Object projectIdObj =
                request.get("projectId");

        String projectId =
                projectIdObj == null
                        ? ""
                        : projectIdObj.toString();

        // 2. Region
        Object regionObj =
                request.get("region");

        String region =
                regionObj == null
                        ? "not specified"
                        : regionObj.toString();

        // 3. Instances
        Object instancesObj =
                request.get("instances");

        int instances = 0;

        if (instancesObj instanceof Number) {
            instances =
                    ((Number) instancesObj).intValue();
        }

        if (instances > 0) {
            plannedChanges.add(
                    "Create " + instances
                            + " compute instance(s)"
            );
        }

        // 4. Load balancer
        Object loadBalancerObj =
                request.get("loadBalancer");

        if (Boolean.TRUE.equals(loadBalancerObj)) {
            plannedChanges.add(
                    "Configure load balancer"
            );
        }

        // 5. CDN
        Object cdnObj =
                request.get("cdn");

        if (Boolean.TRUE.equals(cdnObj)) {
            plannedChanges.add(
                    "Configure CDN"
            );
        }

        // 6. Build simulation response
        result.put(
                "mode",
                "SIMULATION"
        );

        result.put(
                "projectId",
                projectId
        );

        result.put(
                "region",
                region
        );

        result.put(
                "executed",
                false
        );

        result.put(
                "cloudResourcesCreated",
                false
        );

        result.put(
                "plannedChanges",
                plannedChanges
        );

        result.put(
                "status",
                "READY"
        );

        result.put(
                "message",
                "Simulation completed. "
                        + "No cloud resources were created."
        );

        return result;
    }
}