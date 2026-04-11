package com.lays.service;

import com.lays.dto.NoteDTO;
import com.lays.mapper.NoteMapper;
import com.lays.model.Note;
import com.lays.model.User;
import com.lays.repository.NoteRepository;
import com.lays.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NoteService тесты")
class NoteServiceTest {

    @Mock private NoteRepository noteRepository;
    @Mock private UserRepository userRepository;
    @Mock private NoteMapper noteMapper;

    @InjectMocks
    private NoteService noteService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("alice");
        testUser.setEmail("alice@example.com");
        testUser.setPassword("password123");
        testUser.setVerified(true);
    }

    @Test
    @DisplayName("getMyNotes: возвращает заметки пользователя")
    void getMyNotes_returnsUserNotes() {
        // Given
        Note note1 = createTestNote(1L, "Note 1", testUser);
        Note note2 = createTestNote(2L, "Note 2", testUser);

        given(userRepository.findByUsername("alice")).willReturn(Optional.of(testUser));
        given(noteRepository.findByAuthor(testUser)).willReturn(List.of(note1, note2));

        // When
        List<Note> result = noteService.getMyNotes("alice");

        // Then
        assertEquals(2, result.size());
        verify(noteRepository).findByAuthor(testUser);
    }

    @Test
    @DisplayName("getMyNotes: пользователь не найден")
    void getMyNotes_userNotFound_throwsException() {
        // Given
        given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> noteService.getMyNotes("unknown"));
        assertEquals("Пользователь не найден: unknown", ex.getMessage());
    }

    @Test
    @DisplayName("createNote: успешное создание")
    void createNote_success_createsNote() {
        // Given
        NoteDTO dto = new NoteDTO(null, "New Note", "Content", true);

        given(userRepository.findByUsername("alice")).willReturn(Optional.of(testUser));

        // Используем doAnswer для создания заметки
        doAnswer(invocation -> {
            Note noteToSave = invocation.getArgument(0);
            noteToSave.setId(1L);
            return noteToSave;
        }).when(noteRepository).save(any(Note.class));

        // When
        noteService.createNote(dto, "alice");

        // Then
        verify(noteRepository).save(argThat(note ->
                note.getTitle().equals("New Note") &&
                        note.getContent().equals("Content") &&
                        note.isPublic() &&
                        note.getAuthor().equals(testUser)
        ));
    }

    @Test
    @DisplayName("updateNote: успешное обновление")
    void updateNote_success_updatesNote() {
        // Given
        Note existingNote = createTestNote(1L, "Old Title", testUser);
        NoteDTO dto = new NoteDTO(1L, "New Title", "New Content", false);

        given(noteRepository.findById(1L)).willReturn(Optional.of(existingNote));
        given(noteRepository.save(existingNote)).willReturn(existingNote);

        // When
        noteService.updateNote(1L, dto, "alice");

        // Then
        assertEquals("New Title", existingNote.getTitle());
        assertEquals("New Content", existingNote.getContent());
        assertFalse(existingNote.isPublic());
        verify(noteRepository).save(existingNote);
    }

    @Test
    @DisplayName("deleteNote: успешное удаление")
    void deleteNote_success_deletesNote() {
        // Given
        Note note = createTestNote(1L, "To Delete", testUser);
        given(noteRepository.findById(1L)).willReturn(Optional.of(note));

        // When
        noteService.deleteNote(1L, "alice");

        // Then
        verify(noteRepository).delete(note);
    }

    @Test
    @DisplayName("getOwnedNoteForEdit: успешное получение заметки для редактирования")
    void getOwnedNoteForEdit_success_returnsNoteDTO() {
        // Given
        Note note = createTestNote(1L, "Test Note", testUser);
        NoteDTO expectedDTO = new NoteDTO(1L, "Test Note", "Test content for Test Note", false);

        given(noteRepository.findById(1L)).willReturn(Optional.of(note));
        given(noteMapper.toDTO(note)).willReturn(expectedDTO);

        // When
        NoteDTO result = noteService.getOwnedNoteForEdit(1L, "alice");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Note", result.getTitle());
        verify(noteMapper).toDTO(note);
    }

    private Note createTestNote(Long id, String title, User author) {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent("Test content for " + title);
        note.setPublic(false);
        note.setCreatedAt(LocalDateTime.now());
        note.setAuthor(author);
        return note;
    }
}