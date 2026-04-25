package com.lays.aop;

import com.lays.service.BenchmarkMetricsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BenchmarkAspect {

    private final BenchmarkMetricsService benchmarkMetricsService;

    public BenchmarkAspect(BenchmarkMetricsService benchmarkMetricsService) {
        this.benchmarkMetricsService = benchmarkMetricsService;
    }

    @Around("@annotation(com.lays.aop.Benchmark)")
    public Object benchmark(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = buildMethodName(joinPoint);
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            benchmarkMetricsService.recordExecution(methodName, System.nanoTime() - startedAt);
        }
    }

    private String buildMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }
}
