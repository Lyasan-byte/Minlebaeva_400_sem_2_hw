package com.lays.service;

import com.lays.model.Role;
import com.lays.model.User;
import com.lays.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService тесты")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_addsRolePrefixWhenMissing() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setPassword("encoded");
        user.setVerified(true);

        Role role = new Role();
        role.setName("USER");
        user.setRoles(List.of(role));

        given(userRepository.findByUsername("alice")).willReturn(Optional.of(user));

        CustomUserDetails result = (CustomUserDetails) customUserDetailsService.loadUserByUsername("alice");

        assertEquals(1L, result.getId());
        assertEquals("alice", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertTrue(result.isEnabled());
        assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_keepsExistingRolePrefix() {
        User user = new User();
        user.setId(2L);
        user.setUsername("admin");
        user.setPassword("encoded");
        user.setVerified(false);

        Role role = new Role();
        role.setName("ROLE_ADMIN");
        user.setRoles(List.of(role));

        given(userRepository.findByUsername("admin")).willReturn(Optional.of(user));

        CustomUserDetails result = (CustomUserDetails) customUserDetailsService.loadUserByUsername("admin");

        assertFalse(result.isEnabled());
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_throwsWhenUserIsMissing() {
        given(userRepository.findByUsername("ghost")).willReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("ghost")
        );

        assertEquals("Пользователь не найден: ghost", ex.getMessage());
    }
}
