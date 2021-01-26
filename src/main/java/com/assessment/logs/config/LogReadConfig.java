package com.assessment.logs.config;

import com.assessment.logs.composition.ResourceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * loads configuration on the startup, sets default value for few configs if not given by developer
 * instantiates ResourceInfo class which reads the log data at the startup
 */
@Component
public class LogReadConfig {

    @Value("${resource.path}")
    private String resourcePath;

    @Value("${resource.name}")
    private String resourceName;

    @Value("${resource.defaultMessage: No Logs}")
    private String defaultMessage;


    @Bean
    public ResourceInfo getResourceInfo() throws IOException {
        ResourceInfo resourceInfo = new ResourceInfo(resourcePath, resourceName, defaultMessage);
        //once the resource created return the resourceInfo object after reading
        return resourceInfo.read();
    }
}
