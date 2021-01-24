package com.assessment.logs.controller;

import com.assessment.logs.composition.ResourceInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RenderLogsControllerTest {

    @Test
    void verifyRenderLogsFunctionWithDefaultResourceInfoIfFileNotRead() {
        ResourceInfo resourceInfo = new ResourceInfo(null, null, "default message to user if failure");
        RenderLogsController renderLogsController = new RenderLogsController(resourceInfo);
        assertEquals("default message to user if failure", renderLogsController.renderLogs());
    }

    @Test
    void verifyRenderLogsFunctionWithResourceInfoWhenFileIsRead() throws IOException {
        ResourceInfo resourceInfo = new ResourceInfo("/", "fakeLogs", "default message to user if failure").read();
        RenderLogsController renderLogsController = new RenderLogsController(resourceInfo);
        assertEquals("print me test logs", renderLogsController.renderLogs());
    }
}