package com.vinay.logparser.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationLogParserTest {
    private ApplicationLogParser parser;

    @BeforeEach
    void setUp() {
        parser = new ApplicationLogParser();
    }

    @Test
    void testParseLog() {
        parser.parseLog("level=ERROR message=\"Something failed\"");
        parser.parseLog("level=INFO message=\"Starting process\"");
        Map<String, Integer> severityCounts = parser.getSeverityCounts();
        assertEquals(1, severityCounts.get("ERROR"));
        assertEquals(1, severityCounts.get("INFO"));
    }
}
