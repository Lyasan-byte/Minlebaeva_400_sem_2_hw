package com.lays.controller;

import com.lays.dto.BenchmarkMetricsResponse;
import com.lays.dto.MethodExecutionMetricsResponse;
import com.lays.service.BenchmarkMetricsService;
import com.lays.service.MethodExecutionMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetricsController.class)
@Import(MetricsControllerTest.TestSecurityConfig.class)
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MethodExecutionMetricsService methodExecutionMetricsService;

    @MockitoBean
    private BenchmarkMetricsService benchmarkMetricsService;

    private UsernamePasswordAuthenticationToken adminAuthentication;

    @BeforeEach
    void setUp() {
        adminAuthentication = new UsernamePasswordAuthenticationToken(
                "admin",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

    @Test
    void getExecutionMetrics_returnsCollectedCounters() throws Exception {
        given(methodExecutionMetricsService.getAllMetrics()).willReturn(List.of(
                new MethodExecutionMetricsResponse("NoteService.createNote", 3, 1)
        ));

        mockMvc.perform(get("/admin/metrics/executions")
                        .with(authentication(adminAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].methodName").value("NoteService.createNote"))
                .andExpect(jsonPath("$[0].successfulExecutions").value(3))
                .andExpect(jsonPath("$[0].failedExecutions").value(1));
    }

    @Test
    void getBenchmarkMetrics_returnsCollectedBenchmarks() throws Exception {
        given(benchmarkMetricsService.getAllMetrics()).willReturn(List.of(
                new BenchmarkMetricsResponse("HelloService.sayHello", 4, 2.5, 1.0, 4.0, 10.0)
        ));

        mockMvc.perform(get("/admin/metrics/benchmarks")
                        .with(authentication(adminAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].methodName").value("HelloService.sayHello"))
                .andExpect(jsonPath("$[0].executionCount").value(4))
                .andExpect(jsonPath("$[0].averageExecutionTimeMillis").value(2.5));
    }

    @Test
    void getPercentile_returnsCalculatedPercentile() throws Exception {
        given(benchmarkMetricsService.calculatePercentile("HelloService.sayHello", 90))
                .willReturn(new BenchmarkMetricsService.PercentileResult("HelloService.sayHello", 90, 7.5));

        mockMvc.perform(get("/admin/metrics/benchmarks/percentile")
                        .param("method", "HelloService.sayHello")
                        .param("n", "90")
                        .with(authentication(adminAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.methodName").value("HelloService.sayHello"))
                .andExpect(jsonPath("$.percentile").value(90.0))
                .andExpect(jsonPath("$.executionTimeMillis").value(7.5));
    }

    @Test
    void getPercentile_invalidArguments_returnsBadRequest() throws Exception {
        given(benchmarkMetricsService.calculatePercentile("Unknown.method", 200))
                .willThrow(new IllegalArgumentException("Перцентиль должен быть в диапазоне (0, 100]"));

        mockMvc.perform(get("/admin/metrics/benchmarks/percentile")
                        .param("method", "Unknown.method")
                        .param("n", "200")
                        .with(authentication(adminAuthentication))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Перцентиль должен быть в диапазоне (0, 100]"));
    }
}
