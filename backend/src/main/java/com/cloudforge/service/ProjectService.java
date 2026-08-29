package com.cloudforge.service;

import com.cloudforge.model.Project;
import com.cloudforge.model.ProjectStatus;
import com.cloudforge.repository.ProjectRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // CREATE
    public Project createProject(Project project) {

        project.setStatus(ProjectStatus.CREATED);
        project.setCreatedAt(LocalDateTime.now());

        return projectRepository.save(project);
    }

    // READ ALL
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // READ BY ID
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Project not found with id: " + id));
    }

    // UPDATE
    public Project updateProject(Long id, Project updatedProject) {

        Project project = getProjectById(id);

        project.setName(updatedProject.getName());
        project.setDescription(updatedProject.getDescription());

        if (updatedProject.getStatus() != null) {
            project.setStatus(updatedProject.getStatus());
        }

        return projectRepository.save(project);
    }

    // DELETE
    public void deleteProject(Long id) {

        Project project = getProjectById(id);

        projectRepository.delete(project);
    }
}