package com.lays.dto;

public record MethodExecutionMetricsResponse(
        String methodName,
        long successfulExecutions,
        long failedExecutions
) {
}
