package com.lays.service;

import com.lays.aop.Benchmark;
import com.lays.aop.TrackExecutionMetrics;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String sayHello(String name) {
        return "Hello, %s".formatted(name);
    }
}
