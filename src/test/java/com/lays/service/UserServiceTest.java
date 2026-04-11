package com.lays.service;

import com.lays.dto.UserDTO;
import com.lays.mapper.UserMapper;
import com.lays.model.User;
import com.lays.repository.RoleRepository;
import com.lays.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService тесты")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("findAllUsers: возвращает список DTO")
    void findAllUsers_returnsListOfDTOs() {
        // Given
        User user1 = new User(); user1.setId(1L); user1.setUsername("alice");
        User user2 = new User(); user2.setId(2L); user2.setUsername("bob");

        UserDTO dto1 = new UserDTO(1L, "alice");
        UserDTO dto2 = new UserDTO(2L, "bob");

        given(userRepository.findAll()).willReturn(List.of(user1, user2));
        given(userMapper.toDTO(user1)).willReturn(dto1);
        given(userMapper.toDTO(user2)).willReturn(dto2);

        // When
        List<UserDTO> result = userService.findAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals("alice", result.get(0).getUsername());
        assertEquals("bob", result.get(1).getUsername());
        verify(userMapper, times(2)).toDTO(any(User.class));
    }

    @Test
    @DisplayName("findUserById: пользователь найден")
    void findUserById_found_returnsDTO() {
        // Given
        User user = new User(); user.setId(1L); user.setUsername("alice");
        UserDTO expectedDTO = new UserDTO(1L, "alice");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userMapper.toDTO(user)).willReturn(expectedDTO);

        // When
        UserDTO result = userService.findUserById(1L);

        // Then
        assertEquals("alice", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("findUserById: пользователь не найден")
    void findUserById_notFound_throwsException() {
        // Given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findUserById(999L));
        assertEquals("User not found with id: 999", ex.getMessage());
    }

    @Test
    @DisplayName("updateUser: успешное обновление")
    void updateUser_success_returnsUpdatedDTO() {
        // Given
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("alice_old");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("alice_new");

        UserDTO resultDTO = new UserDTO(1L, "alice_new");

        given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
        given(userRepository.findByUsername("alice_new")).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(updatedUser);
        given(userMapper.toDTO(updatedUser)).willReturn(resultDTO);

        // When
        UserDTO result = userService.updateUser(1L, "alice_new");

        // Then
        assertEquals("alice_new", result.getUsername());
        assertEquals("alice_new", existingUser.getUsername()); // entity updated
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("deleteUser: успешное удаление")
    void deleteUser_success_deletesUser() {
        // Given
        given(userRepository.existsById(1L)).willReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser: пользователь не найден")
    void deleteUser_notFound_throwsException() {
        // Given
        given(userRepository.existsById(999L)).willReturn(false);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(999L));
        assertEquals("User not found with id: 999", ex.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("findByUsername: пользователь не найден")
    void findByUsername_notFound_throwsException() {
        // Given
        given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findByUsername("unknown"));
        assertEquals("User not found with username: unknown", ex.getMessage());
    }
}