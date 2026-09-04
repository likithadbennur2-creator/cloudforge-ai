package com.cloudforge.planner;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/infrastructure")
@CrossOrigin(origins = "*")
public class InfrastructurePlannerController {

    private final InfrastructurePlannerService planningService;

    public InfrastructurePlannerController(
            InfrastructurePlannerService planningService) {
        this.planningService = planningService;
    }

    @PostMapping("/plan")
    public String plan(@RequestBody String problem) {
        return planningService.createInfrastructurePlan(problem);
    }
}