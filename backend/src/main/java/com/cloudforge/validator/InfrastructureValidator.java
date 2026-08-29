package com.cloudforge.validator;

import org.springframework.stereotype.Service;

@Service
public class InfrastructureValidator {

    private static final int MAX_INSTANCES = 10;

    public boolean validateInstances(int instances) {
        return instances >= 1 && instances <= MAX_INSTANCES;
    }

    public boolean validatePlan(
            int instances,
            boolean loadBalancer,
            boolean cdn) {

        return validateInstances(instances);
    }
}