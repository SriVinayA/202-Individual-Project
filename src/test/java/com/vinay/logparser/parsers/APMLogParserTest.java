package com.vinay.logparser.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class APMLogParserTest {
    private APMLogParser parser;

    @BeforeEach
    void setUp() {
        parser = new APMLogParser();
    }

    @Test
    void testParseLogAndAggregations() {
        parser.parseLog("metric=cpu_usage value=50");
        parser.parseLog("metric=cpu_usage value=70");
        Map<String, Map<String, Object>> results = parser.calculateAggregations();
        assertEquals(70, results.get("cpu_usage").get("max"));
        assertEquals(50, results.get("cpu_usage").get("min"));
    }
}
