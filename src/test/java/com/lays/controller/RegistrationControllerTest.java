package com.lays.controller;

import com.lays.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@Import(RegistrationControllerTest.TestSecurityConfig.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/register", "/login", "/verification", "/css/**", "/js/**").permitAll()
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

    @Test
    @WithAnonymousUser
    public void testShowRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @WithAnonymousUser
    public void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithAnonymousUser
    public void testRegisterUser_Error_PasswordsDontMatch() throws Exception {
        given(registrationService.register(
                eq("alice"),
                eq("alice@example.com"),
                eq("password123"),
                eq("wrongpass"),
                any(String.class)
        )).willReturn("Пароли не совпадают");

        mockMvc.perform(post("/register")
                        .param("username", "alice")
                        .param("email", "alice@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "wrongpass")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("error", "Пароли не совпадают"))
                .andExpect(model().attribute("username", "alice"))
                .andExpect(model().attribute("email", "alice@example.com"));
    }

    @Test
    @WithAnonymousUser
    public void testRegisterUser_Error_UserExists() throws Exception {
        given(registrationService.register(
                eq("existing"),
                eq("exists@example.com"),
                eq("password123"),
                eq("password123"),
                any(String.class)
        )).willReturn("Пользователь с таким именем уже существует");

        mockMvc.perform(post("/register")
                        .param("username", "existing")
                        .param("email", "exists@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("error", "Пользователь с таким именем уже существует"));
    }

    @Test
    @WithAnonymousUser
    public void testVerifyUser_Success() throws Exception {
        mockMvc.perform(get("/verification")
                        .param("code", "valid-uuid-code"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("message", "Аккаунт подтверждён. Теперь можно войти."));
    }

    @Test
    @WithAnonymousUser
    public void testVerifyUser_Error_InvalidCode() throws Exception {
        willThrow(new RuntimeException("Неверный или устаревший код подтверждения"))
                .given(registrationService).verify("invalid-code");

        mockMvc.perform(get("/verification")
                        .param("code", "invalid-code"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("error", "Неверный или устаревший код подтверждения"));
    }
}