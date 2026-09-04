package com.cloudforge.planner;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class InfrastructureValidator {

    private static final int MAX_INSTANCES = 8;

    private static final Set<String> ALLOWED_REGIONS = Set.of(
            "us-central1",
            "us-east1",
            "asia-south1"
    );

    public void validate(Map<String, Object> action) {

        Object instancesObj = action.get("instances");
        Object regionObj = action.get("region");

        if (instancesObj != null) {

            int instances = ((Number) instancesObj).intValue();

            if (instances <= 0) {
                throw new RuntimeException(
                        "Validation failed: instances must be greater than 0"
                );
            }

            if (instances > MAX_INSTANCES) {
                throw new RuntimeException(
                        "Validation failed: maximum allowed instances is "
                                + MAX_INSTANCES
                );
            }
        }

        if (regionObj != null) {

            String region = regionObj.toString();

            if (!ALLOWED_REGIONS.contains(region)) {
                throw new RuntimeException(
                        "Validation failed: region is not allowed: "
                                + region
                );
            }
        }
    }
}