package com.cloudforge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_analyses")
public class AIAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;

    @Column(columnDefinition = "TEXT")
    private String problem;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    private int instances;

    private boolean loadBalancer;

    private boolean cdn;

    private String priority;

    public AIAnalysis() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public boolean isLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(boolean loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public boolean isCdn() {
        return cdn;
    }

    public void setCdn(boolean cdn) {
        this.cdn = cdn;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}