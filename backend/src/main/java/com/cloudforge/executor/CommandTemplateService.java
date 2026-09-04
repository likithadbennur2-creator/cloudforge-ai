package com.cloudforge.executor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class CommandTemplateService {

    public String loadTemplate(String templatePath) {

        try {
            ClassPathResource resource =
                    new ClassPathResource(templatePath);

            try (InputStream inputStream = resource.getInputStream()) {

                return new String(
                        inputStream.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load command template: " + templatePath,
                    e
            );
        }
    }
}