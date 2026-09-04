package com.cloudforge.executor;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CommandTemplateRenderer {

    public String render(
            String template,
            Map<String, String> variables) {

        String result = template;

        for (Map.Entry<String, String> entry : variables.entrySet()) {

            String placeholder =
                    "${" + entry.getKey() + "}";

            result = result.replace(
                    placeholder,
                    entry.getValue()
            );
        }

        return result;
    }
}