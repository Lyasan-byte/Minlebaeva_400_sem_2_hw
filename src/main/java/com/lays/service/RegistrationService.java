package com.lays.service;

import com.lays.model.Role;
import com.lays.model.User;
import com.lays.repository.RoleRepository;
import com.lays.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String register(String username, String password, String confirmPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Имя пользователя уже занято";
        }

        if (!password.equals(confirmPassword)) {
            return "Пароли не совпадают";
        }

        if (password.length() < 3) {
            return "Пароль должен быть минимум 3 символа";
        }

        try {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Роль USER не найдена в БД"));

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(List.of(userRole));

            userRepository.save(user);

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при сохранении: " + e.getMessage();
        }
    }
}