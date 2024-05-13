package com.vinay.logparser.parsers;

import java.util.*;

public class APMLogParser implements LogParserInterface {
    // Map to store metric names and corresponding values
    private final Map<String, List<Integer>> metrics = new HashMap<>();

    @Override
    public void parseLog(String logEntry) {
        // Split log entry into parts
        String[] parts = logEntry.split(" ");
        String metricName = null;
        int value = 0;

        // Iterate through parts to extract metric name and value
        for (String part : parts) {
            if (part.startsWith("metric=")) {
                // Extract metric name
                metricName = part.substring(part.indexOf('=') + 1);
            } else if (part.startsWith("value=")) {
                // Extract metric value
                value = Integer.parseInt(part.substring(part.indexOf('=') + 1));
            }
        }

        // Add metric value to the corresponding metric name in the map
        if (metricName != null) {
            metrics.computeIfAbsent(metricName, k -> new ArrayList<>()).add(value);
        }
    }

    @Override
    public Map<String, Map<String, Object>> calculateAggregations() {
        // Map to store aggregation results for each metric
        Map<String, Map<String, Object>> aggregationResults = new HashMap<>();

        // Calculate aggregations for each metric
        metrics.forEach((key, values) -> {
            Map<String, Object> stats = new HashMap<>();
            if (!values.isEmpty()) {
                // Sort values
                Collections.sort(values);
                // Calculate and store min, max, average, and median
                stats.put("min", values.get(0));
                stats.put("max", values.get(values.size() - 1));
                stats.put("average", calculateAverage(values));
                stats.put("median", calculateMedian(values));
            }
            // Store stats for the current metric
            aggregationResults.put(key, stats);
        });
        return aggregationResults;
    }

    // Calculate average of a list of integers
    private int calculateAverage(List<Integer> values) {
        return (int) Math.round(values.stream().mapToInt(v -> v).average().orElse(0.0));
    }

    // Calculate median of a list of integers
    private int calculateMedian(List<Integer> values) {
        int size = values.size();
        if (size % 2 == 0) {
            // For even number of elements, take average of middle two values
            return (int) Math.round((values.get(size / 2 - 1) + values.get(size / 2)) / 2.0);
        } else {
            // For odd number of elements, return middle value
            return values.get(size / 2);
        }
    }
}
