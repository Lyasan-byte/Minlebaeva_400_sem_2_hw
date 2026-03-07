package com.lays.service;

import com.lays.dto.UserDTO;
import com.lays.mapper.UserMapper;
import com.lays.model.User;
import com.lays.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO createUser(String username) {
        User user = new User();
        user.setUsername(username);
        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
}