package com.lays.service;

import com.lays.model.Role;
import com.lays.model.User;
import com.lays.properties.MailProperties;
import com.lays.repository.RoleRepository;
import com.lays.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrationService тесты")
class RegistrationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JavaMailSender javaMailSender;
    @Mock private MailProperties mailProperties;
    @Mock private MimeMessage mimeMessage;

    @InjectMocks
    private RegistrationService registrationService;

    private final String TEST_USERNAME = "testuser";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_BASE_URL = "http://localhost:8080";
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");
    }

    @Test
    @DisplayName("register: успешная регистрация")
    void register_success_returnsNullAndSavesUser() throws Exception {
        // Given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encodedPassword");
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(mailProperties.getContent()).thenReturn("Welcome ${name}! Click: ${url}");
        when(mailProperties.getFrom()).thenReturn("noreply@test.com");
        when(mailProperties.getSender()).thenReturn("Test App");
        when(mailProperties.getSubject()).thenReturn("Подтверждение регистрации");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        String result = registrationService.register(
                TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, TEST_BASE_URL
        );

        // Then
        assertNull(result);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(TEST_USERNAME, savedUser.getUsername());
        assertEquals(TEST_EMAIL, savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertFalse(savedUser.isVerified());
        assertNotNull(savedUser.getVerificationCode());
        assertEquals(1, savedUser.getRoles().size());
        assertEquals(userRole, savedUser.getRoles().get(0));

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("register: null email")
    void register_nullEmail_returnsError() {
        // When
        String result = registrationService.register(TEST_USERNAME, null, TEST_PASSWORD, TEST_PASSWORD, TEST_BASE_URL);

        // Then
        assertEquals("Email обязателен", result);
        verify(userRepository, never()).save(any());
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("register: null пароль")
    void register_nullPassword_returnsError() {
        // When
        String result = registrationService.register(TEST_USERNAME, TEST_EMAIL, null, TEST_PASSWORD, TEST_BASE_URL);

        // Then
        assertEquals("Пароль обязателен", result);
        verify(userRepository, never()).save(any());
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

}