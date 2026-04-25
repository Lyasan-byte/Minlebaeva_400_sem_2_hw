package com.lays.dto;

public record BenchmarkMetricsResponse(
        String methodName,
        long executionCount,
        double averageExecutionTimeMillis,
        double minExecutionTimeMillis,
        double maxExecutionTimeMillis,
        double totalExecutionTimeMillis
) {
}
