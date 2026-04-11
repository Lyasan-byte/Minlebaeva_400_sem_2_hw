package com.lays.controller;

import com.lays.dto.UserDTO;
import com.lays.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void testGetUsers_JSON() throws Exception {
        UserDTO user1 = new UserDTO(1L, "alice");
        UserDTO user2 = new UserDTO(2L, "bob");
        given(userService.findAllUsers()).willReturn(List.of(user1, user2));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[1].username").value("bob"));
    }

    @Test
    public void testGetUserById_Success() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "alice");
        given(userService.findUserById(1L)).willReturn(userDTO);

        mockMvc.perform(get("/users/{id}", 1L)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user-detail"))
                .andExpect(model().attribute("user", userDTO));
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        given(userService.findUserById(999L))
                .willThrow(new RuntimeException("User not found with id: 999"));

        mockMvc.perform(get("/users/{id}", 999L)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testShowEditForm_Success() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "alice");
        given(userService.findUserById(1L)).willReturn(userDTO);

        mockMvc.perform(get("/users/{id}/edit", 1L)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"))
                .andExpect(model().attribute("user", userDTO));
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "alice_updated");

        mockMvc.perform(post("/users/{id}/edit", 1L)
                        .flashAttr("user", userDTO)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    public void testUpdateUser_Error_UsernameExists() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "existing_user");
        willThrow(new RuntimeException("User with username existing_user already exists"))
                .given(userService).updateUser(1L, "existing_user");

        mockMvc.perform(post("/users/{id}/edit", 1L)
                        .flashAttr("user", userDTO)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        mockMvc.perform(post("/users/{id}/delete", 1L)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    public void testDeleteUser_Error_NotFound() throws Exception {
        willThrow(new RuntimeException("User not found with id: 999"))
                .given(userService).deleteUser(999L);

        mockMvc.perform(post("/users/{id}/delete", 999L)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }
}