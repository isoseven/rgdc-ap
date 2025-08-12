package com.stkych.rivergreenap.util;

import java.util.List;
import java.util.logging.Logger;

/**
 * Utility for logging execution results in a consistent format.
 */
public final class ExecutionLogger {
    private ExecutionLogger() {
    }

    /**
     * Logs a summary of SQL execution results.
     *
     * @param logger       the logger to use
     * @param patientNumber patient identifier
     * @param status       overall status message
     * @param sqlQueries   executed queries
     * @param successCount number of successful queries
     * @param failureCount number of failed queries
     * @param errors       error messages to log
     */
    public static void logSummary(Logger logger,
                                  int patientNumber,
                                  String status,
                                  List<String> sqlQueries,
                                  int successCount,
                                  int failureCount,
                                  List<String> errors) {
        int total = sqlQueries != null ? sqlQueries.size() : 0;
        logger.info(() -> String.format("%nExecution summary for Patient #%d:%nStatus: %s%nTotal queries: %d%nSuccessful: %d%nFailed: %d",
                patientNumber, status, total, successCount, failureCount));
        if (errors != null && !errors.isEmpty()) {
            logger.warning("\nError details:");
            errors.forEach(err -> logger.warning("- " + err));
        }
    }
}
