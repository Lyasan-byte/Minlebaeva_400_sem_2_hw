package com.lays.controller;

import com.lays.dto.AdminNoteDTO;
import com.lays.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminNoteController.class)
@Import(AdminNoteControllerTest.TestSecurityConfig.class)
public class AdminNoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllNotes_Success() throws Exception {
        // Given
        AdminNoteDTO note1 = new AdminNoteDTO(1L, "Title 1", "Content 1",
                LocalDateTime.now(), true, 10L, "alice");
        AdminNoteDTO note2 = new AdminNoteDTO(2L, "Title 2", "Content 2",
                LocalDateTime.now(), false, 11L, "bob");

        given(noteService.getAllNotesForAdmin()).willReturn(List.of(note1, note2));

        // When & Then
        mockMvc.perform(get("/admin/notes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[0].authorUsername").value("alice"))
                .andExpect(jsonPath("$[1].title").value("Title 2"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllNotes_EmptyList() throws Exception {
        // Given
        given(noteService.getAllNotesForAdmin()).willReturn(List.of());

        // When & Then
        mockMvc.perform(get("/admin/notes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteNote_Success() throws Exception {
        // Given
        Long noteId = 1L;
        willDoNothing().given(noteService).deleteAnyNote(noteId);

        // When & Then
        mockMvc.perform(delete("/admin/notes/{id}", noteId))
                .andExpect(status().isOk())
                .andExpect(content().string("Заметка удалена"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteNote_NotFound() throws Exception {
        // Given
        Long noteId = 999L;
        willDoNothing().given(noteService).deleteAnyNote(noteId);

        // When & Then
        mockMvc.perform(delete("/admin/notes/{id}", noteId))
                .andExpect(status().isOk())
                .andExpect(content().string("Заметка удалена"));
    }
}