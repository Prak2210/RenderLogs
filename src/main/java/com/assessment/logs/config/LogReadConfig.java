package com.assessment.logs.config;

import com.assessment.logs.Builder.ResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class LogReadConfig {
    private static final Logger logger = LoggerFactory.getLogger(LogReadConfig.class);
    private ResourceLoader resourceLoader;

    @Value("${resource.path}")
    private String resourcePath;

    @Value("${resource.name}")
    private String resourceName;

    @Value("${resource.defaultMessage: No Logs}")
    private String defaultMessage;


    @Bean
    public ResourceInfo getResourceBuilder() throws IOException {
        ResourceInfo resourceInfo = new ResourceInfo(resourcePath, resourceName, defaultMessage).read();
        return resourceInfo;
    }
}
