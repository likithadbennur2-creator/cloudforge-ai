package com.cloudforge.executor;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/commands")
@CrossOrigin(origins = "*")
public class CommandController {

    @PostConstruct
    public void testControllerLoaded() {
        System.out.println("🔥 COMMAND CONTROLLER LOADED 🔥");
    }

    private final InfrastructureCommandService infrastructureCommandService;
    private final InfrastructureCommandValidator infrastructureCommandValidator;
    private final InfrastructureSimulationService infrastructureSimulationService;
    private final InfrastructureApprovalService infrastructureApprovalService;

    public CommandController(
            InfrastructureCommandService infrastructureCommandService,
            InfrastructureCommandValidator infrastructureCommandValidator,
            InfrastructureSimulationService infrastructureSimulationService,
            InfrastructureApprovalService infrastructureApprovalService) {

        this.infrastructureCommandService = infrastructureCommandService;
        this.infrastructureCommandValidator = infrastructureCommandValidator;
        this.infrastructureSimulationService = infrastructureSimulationService;
        this.infrastructureApprovalService = infrastructureApprovalService;
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

    @PostMapping("/validate")
    public Map<String, Object> validateCommand(
            @RequestBody Map<String, Object> request) {

        return infrastructureCommandValidator.validate(request);
    }

    @PostMapping("/simulate")
    public Map<String, Object> simulateCommand(
            @RequestBody Map<String, Object> request) {

        return infrastructureSimulationService.simulate(request);

    }
    @PostMapping("/approve")
public Map<String, Object> approveCommand(
        @RequestBody Map<String, Object> request) {

    return infrastructureApprovalService.approve(request);
}
}