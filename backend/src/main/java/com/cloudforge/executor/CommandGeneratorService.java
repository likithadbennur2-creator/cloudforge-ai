package com.cloudforge.executor;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CommandGeneratorService {

    private final CommandTemplateService templateService;
    private final CommandTemplateRenderer templateRenderer;

    public CommandGeneratorService(
            CommandTemplateService templateService,
            CommandTemplateRenderer templateRenderer) {

        this.templateService = templateService;
        this.templateRenderer = templateRenderer;
    }

    public String generateCommand(
            String templatePath,
            Map<String, String> variables) {

        String template =
                templateService.loadTemplate(templatePath);

        return templateRenderer.render(
                template,
                variables
        );
    }
}