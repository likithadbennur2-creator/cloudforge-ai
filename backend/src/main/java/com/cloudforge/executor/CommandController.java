package com.cloudforge.executor;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/commands")
@CrossOrigin(origins = "*")
public class CommandController {

    private final InfrastructureCommandService infrastructureCommandService;

    public CommandController(
            InfrastructureCommandService infrastructureCommandService) {
        this.infrastructureCommandService = infrastructureCommandService;
    }

    @PostMapping("/generate")
    public Map<String, Object> generateCommands(
            @RequestBody Map<String, String> request) {

        String projectId = request.get("projectId");

        if (projectId == null || projectId.isBlank()) {
            throw new RuntimeException("projectId is required");
        }

        return infrastructureCommandService.generateCommands(projectId);
    }
}