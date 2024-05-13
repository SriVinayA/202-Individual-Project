package com.vinay.logparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vinay.logparser.parsers.APMLogParser;
import com.vinay.logparser.parsers.ApplicationLogParser;
import com.vinay.logparser.parsers.RequestLogParser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Check if the correct arguments are provided
        if (args.length < 2 || !"--file".equals(args[0])) {
            System.err.println("Usage: --file <filename.txt>");
            return;
        }

        String filename = args[1];
        APMLogParser apmParser = new APMLogParser();
        ApplicationLogParser appParser = new ApplicationLogParser();
        RequestLogParser reqParser = new RequestLogParser();

        try {
            // Read log entries from the file
            List<String> logEntries = Files.readAllLines(Paths.get(filename));
            for (String logEntry : logEntries) {
                // Determine the log type and parse accordingly
                if (logEntry.contains("metric=")) {
                    apmParser.parseLog(logEntry);
                } else if (logEntry.contains("level=")) {
                    appParser.parseLog(logEntry);
                } else if (logEntry.contains("request_url=")) {
                    reqParser.parseLog(logEntry);
                }
            }

            // Create Gson object with pretty printing
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Output for APM logs
            Files.write(Paths.get("apm.json"), gson.toJson(apmParser.calculateAggregations()).getBytes());

            // Output for Application logs
            Files.write(Paths.get("application.json"), gson.toJson(appParser.getSeverityCounts()).getBytes());

            // Output for Request logs
            Map<String, Map<String, Object>> reqStats = reqParser.calculateAggregations();
            Files.write(Paths.get("request.json"), gson.toJson(reqStats).getBytes());

            System.out.println("Output written to apm.json, application.json, and request.json");

        } catch (Exception e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }
}