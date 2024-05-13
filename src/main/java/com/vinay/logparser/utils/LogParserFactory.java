package com.vinay.logparser.utils;

import com.vinay.logparser.parsers.APMLogParser;
import com.vinay.logparser.parsers.ApplicationLogParser;
import com.vinay.logparser.parsers.LogParserInterface;
import com.vinay.logparser.parsers.RequestLogParser;

// A factory class for creating instances of different log parsers based on the specified log type.
public class LogParserFactory {
    // Static method to get a parser instance based on the provided log type.
    public static LogParserInterface getParser(String logType) {
        // Use a switch statement to determine the appropriate parser based on the log type.
        switch (logType) {
            case "APM":
                return new APMLogParser(); // Return an instance of APMLogParser for APM logs.
            case "Application":
                return new ApplicationLogParser(); // Return an instance of ApplicationLogParser for application logs.
            case "Request":
                return new RequestLogParser(); // Return an instance of RequestLogParser for request logs.
            default:
                throw new IllegalArgumentException("Unknown log type: " + logType); // Throw an exception for unknown log types.
        }
    }
}