package com.vinay.logparser.parsers;

import java.util.Map;

public interface LogParserInterface {
    // Method to parse a log entry
    void parseLog(String logEntry);

    // Method to calculate aggregations
    Map<String, Map<String, Object>> calculateAggregations();
}
