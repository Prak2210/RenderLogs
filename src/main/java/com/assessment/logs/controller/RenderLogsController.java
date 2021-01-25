package com.assessment.logs.controller;

import com.assessment.logs.RenderLogsApplication;
import com.assessment.logs.composition.ResourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This controller takes ResourceInfo object from the bean class LogReadConfig
 * and renders output on server for each request
 */
@Controller
public class RenderLogsController {
    private static final Logger logger = LoggerFactory.getLogger(RenderLogsApplication.class);
    private static long requestCount;
    private ResourceInfo resourceInfo;

    @Autowired
    public RenderLogsController(ResourceInfo resourceInfo) {
        this.resourceInfo = resourceInfo;
    }

    @RequestMapping("logs")
    @ResponseBody
    public String renderLogs() {
        logger.info("logs are from file: {} ", resourceInfo.getResourcePath() + "/" + resourceInfo.getResourceName());
        logger.info("request number {}", ++requestCount);
        return this.resourceInfo.getLogs();
    }
}
