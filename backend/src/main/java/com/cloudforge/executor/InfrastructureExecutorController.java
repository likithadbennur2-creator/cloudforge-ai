package com.cloudforge.executor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/executor")
@CrossOrigin(origins = "*")
public class InfrastructureExecutorController {

    private final InfrastructureExecutor executor;

    public InfrastructureExecutorController(InfrastructureExecutor executor) {
        this.executor = executor;
    }

    @PostMapping("/simulate")
    public String simulate(
            @RequestParam int instances,
            @RequestParam String region) {

        Map<String, Object> action = new HashMap<>();

        action.put("instances", instances);
        action.put("region", region);

        return executor.execute(action);
    }
}