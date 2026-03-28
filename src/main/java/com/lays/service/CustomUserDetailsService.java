package com.lays.service;

import com.lays.model.User;
import com.lays.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> role.getName().startsWith("ROLE_")
                        ? role.getName()
                        : "ROLE_" + role.getName())
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isVerified(),
                authorities
        );
    }
}