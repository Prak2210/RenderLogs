package com.assessment.logs.Builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceInfo {
    private static final Logger logger = LoggerFactory.getLogger(ResourceInfo.class);
    private final String resourcePath;
    private final String resourceName;
    private String logs;

    public ResourceInfo(String resourcePath, String resourceName, String defaultMessage) {
        this.resourceName = resourceName;
        this.resourcePath = trimExtraSlash(resourcePath);
        this.logs = defaultMessage;
    }

    public ResourceInfo read() throws IOException {
        logger.info("Reading api_logs from {} file", resourcePath + "/" + resourceName);
        try {
            Resource resource = new ClassPathResource(resourcePath + "/" + resourceName);
            InputStream inputStream = resource.getInputStream();
            byte[] byteData = FileCopyUtils.copyToByteArray(inputStream);
            this.logs = new String(byteData, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            logger.info("Exception occurred while reading the file: {}", e.getMessage());
        }
        return this;
    }

    public String getLogs() {
        return logs;
    }

    private String trimExtraSlash(String resourcePath) {
        if (resourcePath != null && resourcePath.endsWith("/")) {
            resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
        }
        return resourcePath;
    }
}
