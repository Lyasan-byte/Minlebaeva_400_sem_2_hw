package com.lays.dto;

public record PercentileResponse(
        String methodName,
        double percentile,
        double executionTimeMillis
) {
}
