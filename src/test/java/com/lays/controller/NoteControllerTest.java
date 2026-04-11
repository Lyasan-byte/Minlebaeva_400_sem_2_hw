package com.lays.controller;

import com.lays.dto.NoteDTO;
import com.lays.model.Note;
import com.lays.model.User;
import com.lays.service.CustomUserDetails;
import com.lays.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
@Import(NoteControllerTest.TestSecurityConfig.class)
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    private CustomUserDetails testUser;
    private UsernamePasswordAuthenticationToken testAuthentication;

    @BeforeEach
    void setUp() {
        testUser = new CustomUserDetails(
                1L,
                "alice",
                "password123",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        testAuthentication = new UsernamePasswordAuthenticationToken(
                testUser,
                null,
                testUser.getAuthorities()
        );
    }

    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/notes/public", "/login", "/register", "/verification", "/css/**", "/js/**").permitAll()
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

    // ===== Хелпер для создания тестовых заметок =====
    private Note createTestNote(Long id, String title, String authorUsername) {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent("Test content");
        note.setPublic(false);
        note.setCreatedAt(LocalDateTime.now());

        User author = new User();
        author.setUsername(authorUsername);
        note.setAuthor(author);

        return note;
    }

    @Test
    public void testGetMyNotes_WithoutKeyword() throws Exception {
        Note note = createTestNote(1L, "My Note", "alice");
        given(noteService.getMyNotes("alice")).willReturn(List.of(note));

        mockMvc.perform(get("/notes")
                        .with(authentication(testAuthentication))  // 👈 Авторизация
                        .with(csrf()))  // 👈 CSRF-токен
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attribute("notes", hasSize(1)));
    }

    @Test
    public void testGetMyNotes_WithKeyword() throws Exception {
        Note note = createTestNote(1L, "Search Result", "alice");
        given(noteService.searchNotesByTitle("test")).willReturn(List.of(note));

        mockMvc.perform(get("/notes")
                        .param("keyword", "test")
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attribute("notes", hasSize(1)))
                .andExpect(model().attribute("keyword", "test"));
    }

    @Test
    public void testGetPublicNotes_WithoutKeyword() throws Exception {
        Note publicNote = createTestNote(1L, "Public Note", "bob");
        publicNote.setPublic(true);
        given(noteService.getPublicNotes()).willReturn(List.of(publicNote));

        // 👈 Публичный эндпоинт — без авторизации, но с CSRF
        mockMvc.perform(get("/notes/public")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("public_notes"))
                .andExpect(model().attribute("notes", hasSize(1)));
    }

    @Test
    public void testGetPublicNotes_WithKeyword_Filtered() throws Exception {
        Note publicNote = createTestNote(1L, "Public Search", "bob");
        publicNote.setPublic(true);

        Note privateNote = createTestNote(2L, "Private", "alice");
        privateNote.setPublic(false);

        given(noteService.searchNotesByTitle("search")).willReturn(List.of(publicNote, privateNote));

        mockMvc.perform(get("/notes/public")
                        .param("keyword", "search")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("public_notes"))
                .andExpect(model().attribute("notes", hasSize(1)));
    }

    @Test
    public void testShowCreateForm() throws Exception {
        // 👈 Форма создания — требует авторизации + CSRF
        mockMvc.perform(get("/notes/create")
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("formTitle", "Создать заметку"));
    }

    @Test
    public void testCreateNote_Success() throws Exception {
        NoteDTO noteDTO = new NoteDTO(null, "New Note", "Content", true);

        mockMvc.perform(post("/notes/create")
                        .flashAttr("note", noteDTO)
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));
    }

    @Test
    public void testCreateNote_Error() throws Exception {
        NoteDTO noteDTO = new NoteDTO(null, "", "Content", true);

        willThrow(new RuntimeException("Заголовок не может быть пустым"))
                .given(noteService)
                .createNote(any(NoteDTO.class), eq("alice"));

        mockMvc.perform(post("/notes/create")
                        .flashAttr("note", noteDTO)
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Заголовок не может быть пустым"));
    }

    @Test
    public void testShowEditForm_Success() throws Exception {
        NoteDTO noteDTO = new NoteDTO(1L, "Edit Me", "Content", false);
        given(noteService.getOwnedNoteForEdit(1L, "alice")).willReturn(noteDTO);

        mockMvc.perform(get("/notes/{id}/edit", 1L)
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attribute("formTitle", "Редактировать заметку"));
    }

    @Test
    public void testShowEditForm_Error_NotOwned() throws Exception {
        willThrow(new RuntimeException("Заметка не найдена"))
                .given(noteService)
                .getOwnedNoteForEdit(999L, "alice");

        mockMvc.perform(get("/notes/{id}/edit", 999L)
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testEditNote_Success() throws Exception {
        NoteDTO noteDTO = new NoteDTO(1L, "Updated", "New Content", true);

        mockMvc.perform(post("/notes/{id}/edit", 1L)
                        .flashAttr("note", noteDTO)
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));
    }

    @Test
    public void testEditNote_Error() throws Exception {
        NoteDTO noteDTO = new NoteDTO(1L, "Updated", "Content", true);

        willThrow(new RuntimeException("Ошибка обновления"))
                .given(noteService)
                .updateNote(eq(1L), any(NoteDTO.class), eq("alice"));

        mockMvc.perform(post("/notes/{id}/edit", 1L)
                        .flashAttr("note", noteDTO)
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testDeleteNote_Success() throws Exception {
        mockMvc.perform(post("/notes/{id}/delete", 1L)
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));
    }

    @Test
    public void testDeleteNote_Error() throws Exception {
        willThrow(new RuntimeException("Ошибка удаления"))
                .given(noteService)
                .deleteNote(999L, "alice");

        mockMvc.perform(post("/notes/{id}/delete", 999L)
                        .with(authentication(testAuthentication))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }
}