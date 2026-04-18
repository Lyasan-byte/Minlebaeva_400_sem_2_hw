package com.lays.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void exposesProvidedUserFields() {
        CustomUserDetails details = new CustomUserDetails(
                42L,
                "alice",
                "secret",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        assertEquals(42L, details.getId());
        assertEquals("alice", details.getUsername());
        assertEquals("secret", details.getPassword());
        assertTrue(details.isEnabled());
        assertEquals(1, details.getAuthorities().size());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
    }

    @Test
    void keepsDisabledFlag() {
        CustomUserDetails details = new CustomUserDetails(
                7L,
                "bob",
                "pwd",
                false,
                List.of()
        );

        assertFalse(details.isEnabled());
    }
}
