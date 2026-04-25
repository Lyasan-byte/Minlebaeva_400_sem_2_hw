package com.lays.service;

import com.lays.dto.BenchmarkMetricsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Service
public class BenchmarkMetricsService {

    private static final double NANOS_IN_MILLISECOND = 1_000_000.0;

    private final ConcurrentMap<String, BenchmarkCounter> counters = new ConcurrentHashMap<>();

    public void recordExecution(String methodName, long durationNanos) {
        BenchmarkCounter counter = counters.computeIfAbsent(methodName, key -> new BenchmarkCounter());
        counter.executionCount().increment();
        counter.totalDurationNanos().add(durationNanos);
        counter.durationsNanos().add(durationNanos);
        updateMin(counter.minDurationNanos(), durationNanos);
        updateMax(counter.maxDurationNanos(), durationNanos);
    }

    public List<BenchmarkMetricsResponse> getAllMetrics() {
        return counters.entrySet().stream()
                .map(entry -> toResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(BenchmarkMetricsResponse::methodName))
                .toList();
    }

    public PercentileResult calculatePercentile(String methodName, double percentile) {
        validatePercentile(percentile);

        BenchmarkCounter counter = counters.get(methodName);
        if (counter == null || counter.durationsNanos().isEmpty()) {
            throw new IllegalArgumentException("Статистика для метода не найдена: " + methodName);
        }

        List<Long> sortedDurations = new ArrayList<>(counter.durationsNanos());
        sortedDurations.sort(Long::compareTo);

        int index = (int) Math.ceil((percentile / 100.0) * sortedDurations.size()) - 1;
        int safeIndex = Math.max(0, Math.min(index, sortedDurations.size() - 1));

        return new PercentileResult(methodName, percentile, nanosToMillis(sortedDurations.get(safeIndex)));
    }

    private BenchmarkMetricsResponse toResponse(String methodName, BenchmarkCounter counter) {
        long executionCount = counter.executionCount().sum();
        if (executionCount == 0) {
            return new BenchmarkMetricsResponse(methodName, 0, 0, 0, 0, 0);
        }

        return new BenchmarkMetricsResponse(
                methodName,
                executionCount,
                nanosToMillis(counter.totalDurationNanos().sum()) / executionCount,
                nanosToMillis(counter.minDurationNanos().get()),
                nanosToMillis(counter.maxDurationNanos().get()),
                nanosToMillis(counter.totalDurationNanos().sum())
        );
    }

    private void validatePercentile(double percentile) {
        if (percentile <= 0 || percentile > 100) {
            throw new IllegalArgumentException("Перцентиль должен быть в диапазоне (0, 100]");
        }
    }

    private void updateMin(AtomicLong currentMin, long candidate) {
        currentMin.accumulateAndGet(candidate, Math::min);
    }

    private void updateMax(AtomicLong currentMax, long candidate) {
        currentMax.accumulateAndGet(candidate, Math::max);
    }

    private double nanosToMillis(long nanos) {
        return nanos / NANOS_IN_MILLISECOND;
    }

    private record BenchmarkCounter(
            LongAdder executionCount,
            LongAdder totalDurationNanos,
            AtomicLong minDurationNanos,
            AtomicLong maxDurationNanos,
            ConcurrentLinkedQueue<Long> durationsNanos
    ) {
        private BenchmarkCounter() {
            this(
                    new LongAdder(),
                    new LongAdder(),
                    new AtomicLong(Long.MAX_VALUE),
                    new AtomicLong(Long.MIN_VALUE),
                    new ConcurrentLinkedQueue<>()
            );
        }
    }

    public record PercentileResult(String methodName, double percentile, double executionTimeMillis) {
    }
}
