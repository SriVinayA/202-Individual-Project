package com.vinay.logparser.parsers;

import java.util.*;

public class RequestLogParser implements LogParserInterface {
    // Map to store response times by route
    private final Map<String, List<Integer>> responseTimesByRoute = new HashMap<>();
    // Map to store status code counts by route
    private final Map<String, Map<String, Integer>> statusCodeCountsByRoute = new HashMap<>();

    @Override
    public void parseLog(String logEntry) {
        // Split log entry into parts
        String[] parts = logEntry.split("\\s+");
        String url = null;
        int responseTime = 0;
        String statusCode = null;

        // Iterate through parts to extract URL, response time, and status code
        for (String part : parts) {
            if (part.startsWith("request_url=")) {
                // Extract URL
                url = part.substring("request_url=".length()).replaceAll("\"", "");
            } else if (part.startsWith("response_time_ms=")) {
                // Extract response time
                responseTime = Integer.parseInt(part.substring("response_time_ms=".length()));
            } else if (part.startsWith("response_status=")) {
                // Extract status code
                statusCode = part.substring("response_status=".length());
            }
        }

        // Add response time and status code to the corresponding route
        if (url != null) {
            // Update response times by route
            responseTimesByRoute.computeIfAbsent(url, k -> new ArrayList<>()).add(responseTime);
            if (statusCode != null) {
                // Update status code counts by route
                String statusCategory = statusCode.charAt(0) + "XX";
                statusCodeCountsByRoute.computeIfAbsent(url, k -> new HashMap<>()).merge(statusCategory, 1, Integer::sum);
            }
        }
    }

    @Override
    public Map<String, Map<String, Object>> calculateAggregations() {
        // Map to store final statistics
        Map<String, Map<String, Object>> finalStats = new HashMap<>();

        // Calculate statistics for each route
        for (Map.Entry<String, List<Integer>> entry : responseTimesByRoute.entrySet()) {
            Map<String, Object> stats = new HashMap<>();
            List<Integer> times = entry.getValue();
            Collections.sort(times);
            if (!times.isEmpty()) {
                // Calculate response time statistics
                Map<String, Object> responseTimesStats = new LinkedHashMap<>();
                responseTimesStats.put("min", times.get(0));
                responseTimesStats.put("50_percentile", calculatePercentile(times, 50));
                responseTimesStats.put("90_percentile", calculatePercentile(times, 90));
                responseTimesStats.put("95_percentile", calculatePercentile(times, 95));
                responseTimesStats.put("99_percentile", calculatePercentile(times, 99));
                responseTimesStats.put("max", times.get(times.size() - 1));

                // Add response time statistics and status code counts to final statistics
                stats.put("response_times", responseTimesStats);
                stats.put("status_codes", statusCodeCountsByRoute.get(entry.getKey()));
            }
            finalStats.put(entry.getKey(), stats);
        }
        return finalStats;
    }

    // Method to calculate percentile from a list of values
    private int calculatePercentile(List<Integer> values, int percentile) {
        // Check for null or empty list
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("The list cannot be null or empty");
        }

        // Ensure the list is sorted
        List<Integer> sortedValues = new ArrayList<>(values);
        Collections.sort(sortedValues);

        // Calculate percentile index
        double index = (percentile / 100.0) * (sortedValues.size() - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);

        // Handle cases when lower and upper indexes are equal
        if (lowerIndex == upperIndex) {
            return sortedValues.get(lowerIndex);
        }

        // Calculate percentile value using linear interpolation
        double weight = index - lowerIndex;
        double result = sortedValues.get(lowerIndex) * (1 - weight) + sortedValues.get(upperIndex) * weight;
        return (int) Math.round(result);
    }
}
