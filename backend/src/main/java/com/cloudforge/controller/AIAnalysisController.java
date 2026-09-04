package com.cloudforge.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudforge.model.AIAnalysis;
import com.cloudforge.repository.AIAnalysisRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/analyses")
public class AIAnalysisController {

    private final AIAnalysisRepository repository;

    public AIAnalysisController(AIAnalysisRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AIAnalysis> getAllAnalyses() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public AIAnalysis getAnalysis(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Analysis not found with id: " + id));
    }
}