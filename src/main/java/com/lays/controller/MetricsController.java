package com.lays.controller;

import com.lays.dto.BenchmarkMetricsResponse;
import com.lays.dto.MethodExecutionMetricsResponse;
import com.lays.dto.PercentileResponse;
import com.lays.service.BenchmarkMetricsService;
import com.lays.service.MethodExecutionMetricsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/metrics")
public class MetricsController {

    private final MethodExecutionMetricsService methodExecutionMetricsService;
    private final BenchmarkMetricsService benchmarkMetricsService;

    public MetricsController(MethodExecutionMetricsService methodExecutionMetricsService,
                             BenchmarkMetricsService benchmarkMetricsService) {
        this.methodExecutionMetricsService = methodExecutionMetricsService;
        this.benchmarkMetricsService = benchmarkMetricsService;
    }

    @GetMapping("/executions")
    public List<MethodExecutionMetricsResponse> getExecutionMetrics() {
        return methodExecutionMetricsService.getAllMetrics();
    }

    @GetMapping("/benchmarks")
    public List<BenchmarkMetricsResponse> getBenchmarkMetrics() {
        return benchmarkMetricsService.getAllMetrics();
    }

    @GetMapping("/benchmarks/percentile")
    public PercentileResponse getPercentile(@RequestParam("method") String methodName,
                                            @RequestParam("n") double percentile) {
        BenchmarkMetricsService.PercentileResult result =
                benchmarkMetricsService.calculatePercentile(methodName, percentile);
        return new PercentileResponse(
                result.methodName(),
                result.percentile(),
                result.executionTimeMillis()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        return exception.getMessage();
    }
}
