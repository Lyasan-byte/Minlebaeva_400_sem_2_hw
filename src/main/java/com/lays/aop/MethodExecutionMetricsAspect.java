package com.lays.aop;

import com.lays.service.MethodExecutionMetricsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodExecutionMetricsAspect {

    private final MethodExecutionMetricsService methodExecutionMetricsService;

    public MethodExecutionMetricsAspect(MethodExecutionMetricsService methodExecutionMetricsService) {
        this.methodExecutionMetricsService = methodExecutionMetricsService;
    }

    @Around("@annotation(com.lays.aop.TrackExecutionMetrics)")
    public Object trackExecutionMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = buildMethodName(joinPoint);
        try {
            Object result = joinPoint.proceed();
            methodExecutionMetricsService.recordSuccess(methodName);
            return result;
        } catch (Throwable throwable) {
            methodExecutionMetricsService.recordFailure(methodName);
            throw throwable;
        }
    }

    private String buildMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }
}
