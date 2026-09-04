package com.cloudforge.ai;

import com.cloudforge.model.AIAnalysis;
import com.cloudforge.model.Project;
import com.cloudforge.repository.AIAnalysisRepository;
import com.cloudforge.repository.ProjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
    origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
    }
)
@RestController
@RequestMapping("/api/ai")
public class GeminiController {

    private final GeminiService geminiService;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;

    public GeminiController(
            GeminiService geminiService,
            AIAnalysisRepository aiAnalysisRepository,
            ProjectRepository projectRepository,
            ObjectMapper objectMapper) {

        this.geminiService = geminiService;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.projectRepository = projectRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/analyze")
    public AIAnalysis analyze(@RequestBody GeminiRequest request) {

        // 1. Find the project
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Project not found with id: " + request.getProjectId()
                        )
                );

        // 2. Send problem to Gemini
        AIAnalysisResponse response =
                geminiService.analyzeProblem(request.getProblem());

        // 3. Convert Gemini's structured response into JSON
        String geminiJson;

        try {
            geminiJson = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to convert Gemini response to JSON", e
            );
        }

        // 4. Store complete Gemini JSON inside projects.ai_analysis
        project.setAiAnalysis(geminiJson);

        // 5. Save project
        projectRepository.save(project);

        // 6. Keep existing AIAnalysis storage for now
        AIAnalysis analysis = new AIAnalysis();

        analysis.setProjectId(project.getId());
        analysis.setProblem(response.getProblem());
        analysis.setRecommendation(response.getRecommendation());
        analysis.setInstances(response.getInstances());
        analysis.setLoadBalancer(response.isLoadBalancer());
        analysis.setCdn(response.isCdn());
        analysis.setPriority(response.getPriority());

        return aiAnalysisRepository.save(analysis);
    }
}