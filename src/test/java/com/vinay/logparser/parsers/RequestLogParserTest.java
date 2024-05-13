package com.vinay.logparser.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestLogParserTest {
    private RequestLogParser parser;

    @BeforeEach
    void setUp() {
        parser = new RequestLogParser();
    }

    @Test
    void testParseLog() {
        parser.parseLog("request_url=/api/test response_time_ms=200 response_status=200");
        Map<String, Map<String, Object>> results = parser.calculateAggregations();

        // Cast is necessary to inform the compiler of the specific generic type
        Map<String, Object> responseTimes = (Map<String, Object>) results.get("/api/test").get("response_times");
        assertNotNull(results.get("/api/test"));
        assertEquals(200, responseTimes.get("min"));
    }

    @Test
    void testCalculatePercentile() {
        parser.parseLog("request_url=/api/test response_time_ms=100 response_status=200");
        parser.parseLog("request_url=/api/test response_time_ms=300 response_status=200");
        Map<String, Map<String, Object>> results = parser.calculateAggregations();

        Map<String, Object> responseTimes = (Map<String, Object>) results.get("/api/test").get("response_times");
        assertEquals(100, responseTimes.get("min"));
        assertEquals(300, responseTimes.get("max"));
        assertEquals(200, responseTimes.get("50_percentile"));
    }
}
