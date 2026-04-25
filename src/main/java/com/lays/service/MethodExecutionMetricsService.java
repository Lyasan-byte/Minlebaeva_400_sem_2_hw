package com.lays.service;

import com.lays.dto.MethodExecutionMetricsResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

@Service
public class MethodExecutionMetricsService {

    private final ConcurrentMap<String, MethodExecutionCounter> counters = new ConcurrentHashMap<>();

    public void recordSuccess(String methodName) {
        counters.computeIfAbsent(methodName, key -> new MethodExecutionCounter()).successfulExecutions().increment();
    }

    public void recordFailure(String methodName) {
        counters.computeIfAbsent(methodName, key -> new MethodExecutionCounter()).failedExecutions().increment();
    }

    public List<MethodExecutionMetricsResponse> getAllMetrics() {
        return counters.entrySet().stream()
                .map(entry -> new MethodExecutionMetricsResponse(
                        entry.getKey(),
                        entry.getValue().successfulExecutions().sum(),
                        entry.getValue().failedExecutions().sum()
                ))
                .sorted(Comparator.comparing(MethodExecutionMetricsResponse::methodName))
                .toList();
    }

    private record MethodExecutionCounter(LongAdder successfulExecutions, LongAdder failedExecutions) {
        private MethodExecutionCounter() {
            this(new LongAdder(), new LongAdder());
        }
    }
}
