package com.cloudforge.ai;

public class AIAnalysisResponse {

    private String problem;
    private String recommendation;
    private int instances;
    private boolean loadBalancer;
    private boolean cdn;
    private String priority;

    public AIAnalysisResponse() {
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