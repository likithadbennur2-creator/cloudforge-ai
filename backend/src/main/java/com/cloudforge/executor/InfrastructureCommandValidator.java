package com.cloudforge.executor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class InfrastructureCommandValidator {

    public Map<String, Object> validate(
            Map<String, Object> request) {

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 1. Validate project ID
        Object projectIdObj = request.get("projectId");

        if (projectIdObj == null ||
                projectIdObj.toString().isBlank()) {
            errors.add("projectId is required");
        }

        // 2. Validate generated commands
        Object commandsObj = request.get("commands");

        if (commandsObj == null) {

            errors.add("commands are required");

        } else if (!(commandsObj instanceof List<?>)) {

            errors.add("commands must be a list");

        } else {

            List<?> commands = (List<?>) commandsObj;

            if (commands.isEmpty()) {

                errors.add("commands list cannot be empty");

            } else {

                for (Object commandObj : commands) {

                    if (commandObj == null ||
                            commandObj.toString().isBlank()) {

                        errors.add("generated command cannot be empty");
                        continue;
                    }

                    String command = commandObj.toString();

                    if (command.contains("${")) {
                        errors.add(
                                "command contains unresolved variables: "
                                        + command);
                    }

                    if (command.contains("rm -rf") ||
                            command.contains("shutdown") ||
                            command.contains("reboot")) {

                        errors.add(
                                "unsafe command detected: "
                                        + command);
                    }
                }
            }
        }

        // 3. Validate region
        Object regionObj = request.get("region");

        if (regionObj == null ||
                regionObj.toString().isBlank()) {

            warnings.add("region is not specified");
        }

        // 4. Validate instance count
        Object instancesObj = request.get("instances");

        if (instancesObj != null) {

            try {

                int instances =
                        ((Number) instancesObj).intValue();

                if (instances <= 0) {
                    errors.add("instances must be greater than 0");
                }

                if (instances > 8) {
                    errors.add("maximum allowed instances is 8");
                }

            } catch (Exception e) {

                errors.add("instances must be a number");
            }
        }

        // 5. Build validation response
        Map<String, Object> result =
                new LinkedHashMap<>();

        boolean valid = errors.isEmpty();

        result.put("valid", valid);

        result.put(
                "status",
                valid
                        ? (warnings.isEmpty()
                            ? "VALID"
                            : "WARNING")
                        : "INVALID"
        );

        result.put("errors", errors);
        result.put("warnings", warnings);

        return result;
    }
}