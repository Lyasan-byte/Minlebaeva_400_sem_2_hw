package com.lays.service;

import com.lays.model.User;
import com.lays.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(u -> {
            List<String> roleNames = u.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());

            return new CustomUserDetails(
                    u.getId(),
                    u.getUsername(),
                    u.getPassword(),
                    roleNames
            );
        }).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}