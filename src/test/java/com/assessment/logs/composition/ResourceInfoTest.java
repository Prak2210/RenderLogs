package com.assessment.logs.composition;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceInfoTest {

    @Test
    void verifyBasicReadFromFile() throws IOException {
        ResourceInfo resourceInfo = new ResourceInfo("", "fakeLogs", "default message if resource not found");

        // before reading file logs
        assertEquals("default message if resource not found", resourceInfo.getLogs());
        //after reading file logs
        assertEquals("print me test logs", resourceInfo.read().getLogs());
    }

    @Test
    void verifyReadFromFileIfPathHasTrailingSlash() throws IOException {
        ResourceInfo resourceInfo = new ResourceInfo("/", "fakeLogs", "default message if resource not found");

        // before reading file logs
        assertEquals("default message if resource not found", resourceInfo.getLogs());
        //after reading file logs
        assertEquals("print me test logs", resourceInfo.read().getLogs());
    }

    @Test
    void verifyReadFromFileIfPathHasHierarchy() throws IOException {
        ResourceInfo resourceInfo = new ResourceInfo("/1/2/3/4/", "logs", "default message if resource not found");

        // before reading file logs
        assertEquals("default message if resource not found", resourceInfo.getLogs());
        //after reading file logs
        assertEquals("I am inside 1/2/3/4 and you found me", resourceInfo.read().getLogs());
    }

    @Test
    void verifyReadFromFilePreservesFormatting() throws IOException {
        ResourceInfo resourceInfo = new ResourceInfo("/", "complexLogs", "default message if resource not found");

        // before reading file logs
        assertEquals("default message if resource not found", resourceInfo.getLogs());
        //after reading file logs it preserves all new line chars
        assertEquals("I have < > & // ^ * #\nalso I am multi line", resourceInfo.read().getLogs());
    }

    @Test
    void verifyGetLogsIfResourceUnavailable() {
        ResourceInfo resourceInfo = new ResourceInfo("default path", "default name", "default message if resource not found");
        assertEquals("default message if resource not found", resourceInfo.getLogs());
    }

    @Test
    void verifyResourcePathAndName() {
        ResourceInfo resourceInfo = new ResourceInfo("default path", "default name", "default message if resource not found");
        assertEquals("default path", resourceInfo.getResourcePath());
        assertEquals("default name", resourceInfo.getResourceName());
    }
}