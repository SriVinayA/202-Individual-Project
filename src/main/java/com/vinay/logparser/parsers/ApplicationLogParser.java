package com.vinay.logparser.parsers;

import java.util.HashMap;
import java.util.Map;

public class ApplicationLogParser implements LogParserInterface {
    // Map to store counts of different severity levels
    private final Map<String, Integer> severityCounts = new HashMap<>();

    @Override
    public void parseLog(String logEntry) {
        // Extract the severity level from the log entry
        String level = extractLevel(logEntry);

        // Update the count of severity levels
        if (level != null) {
            severityCounts.merge(level, 1, Integer::sum);
        }
    }

    @Override
    public Map<String, Map<String, Object>> calculateAggregations() {
        // Map to store aggregated data
        Map<String, Map<String, Object>> aggregatedData = new HashMap<>();
        // Store severity counts in aggregated data
        aggregatedData.put("severity_counts", new HashMap<>(severityCounts));
        return aggregatedData;
    }

    // Getter method to access severityCounts map
    public Map<String, Integer> getSeverityCounts() {
        return severityCounts;
    }

    // Helper method to extract the severity level from a log entry
    private String extractLevel(String logEntry) {
        // Iterate through each part of the log entry
        for (String part : logEntry.split(" ")) {
            // Extract the value for "level" if found
            if (part.startsWith("level=")) {
                // Remove potential quotes and return the severity level
                return part.substring(part.indexOf('=') + 1).replaceAll("\"", "");
            }
        }
        // Return null if "level" is not found in the log entry
        return null;
    }
}
