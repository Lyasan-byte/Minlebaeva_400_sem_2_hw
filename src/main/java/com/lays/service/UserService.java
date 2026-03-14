package com.lays.service;

import com.lays.dto.RegistrationDTO;
import com.lays.dto.UserDTO;
import com.lays.mapper.UserMapper;
import com.lays.model.Role;
import com.lays.model.User;
import com.lays.repository.RoleRepository;
import com.lays.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO createUser(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User with username " + username + " already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("temp123"));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        user.setRoles(List.of(userRole));

        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Transactional
    public UserDTO registerUser(RegistrationDTO registrationDTO) {
        if (userRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new RuntimeException("Пароли не совпадают");
        }

        if (registrationDTO.getPassword().length() < 4) {
            throw new RuntimeException("Пароль должен содержать минимум 4 символа");
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена в системе"));
        user.setRoles(List.of(userRole));

        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Transactional
    public UserDTO updateUser(Long id, String username) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!user.getUsername().equals(username) &&
                userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User with username " + username + " already exists");
        }

        user.setUsername(username);
        User updated = userRepository.save(user);
        return userMapper.toDTO(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return userMapper.toDTO(user);
    }
}