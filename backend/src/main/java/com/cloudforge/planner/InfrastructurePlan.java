package com.cloudforge.planner;

public class InfrastructurePlan {

    private int instances;
    private String region;
    private boolean loadBalancer;
    private boolean cdn;

    public InfrastructurePlan() {
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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
}