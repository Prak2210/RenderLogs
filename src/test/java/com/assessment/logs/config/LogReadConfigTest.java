package com.assessment.logs.config;

import com.assessment.logs.RenderLogsApplication;
import com.assessment.logs.composition.ResourceInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RenderLogsApplication.class, initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
class LogReadConfigTest {

    @Autowired
    private LogReadConfig logReadConfig;
    private ResourceInfo resourceInfo;

    @Test
    void checkConfigurationLoadingFromYamlFile() throws IOException {
        // verifying if configurations are loaded properly
        this.resourceInfo = this.logReadConfig.getResourceInfo();
        assertEquals("file not found but defaults were set properly", resourceInfo.getLogs());
        assertEquals("anything", resourceInfo.getResourcePath());
        assertEquals("any name", resourceInfo.getResourceName());
    }
}