package com.lays.service;

import com.lays.dto.AdminNoteDTO;
import com.lays.dto.NoteDTO;
import com.lays.mapper.NoteMapper;
import com.lays.model.Note;
import com.lays.model.User;
import com.lays.repository.NoteRepository;
import com.lays.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository,
                       UserRepository userRepository,
                       NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.noteMapper = noteMapper;
    }

    public List<Note> getMyNotes(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));
        return noteRepository.findByAuthor(user);
    }

    public List<Note> getPublicNotes() {
        return noteRepository.findByIsPublicTrue();
    }

    public List<Note> searchNotesByTitle(String keyword) {
        return noteRepository.findByTitleContaining(keyword);
    }

    public NoteDTO getOwnedNoteForEdit(Long id, String username) {
        Note note = getOwnedNote(id, username);
        return noteMapper.toDTO(note);
    }

    public Note getOwnedNote(Long id, String username) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заметка не найдена"));

        if (!note.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("Нельзя редактировать чужую заметку");
        }

        return note;
    }

    @Transactional
    public void createNote(NoteDTO noteDTO, String username) {
        if (noteDTO.getTitle() == null || noteDTO.getTitle().isBlank()) {
            throw new RuntimeException("Заголовок не должен быть пустым");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        Note note = new Note();
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setPublic(noteDTO.isPublic());
        note.setCreatedAt(LocalDateTime.now());
        note.setAuthor(user);

        noteRepository.save(note);
    }

    @Transactional
    public void updateNote(Long id, NoteDTO noteDTO, String username) {
        if (noteDTO.getTitle() == null || noteDTO.getTitle().isBlank()) {
            throw new RuntimeException("Заголовок не должен быть пустым");
        }

        Note note = getOwnedNote(id, username);
        note.setTitle(noteDTO.getTitle());
        note.setContent(noteDTO.getContent());
        note.setPublic(noteDTO.isPublic());

        noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long id, String username) {
        Note note = getOwnedNote(id, username);
        noteRepository.delete(note);
    }

    public List<AdminNoteDTO> getAllNotesForAdmin() {
        return noteRepository.findAll().stream()
                .map(note -> new AdminNoteDTO(
                        note.getId(),
                        note.getTitle(),
                        note.getContent(),
                        note.getCreatedAt(),
                        note.isPublic(),
                        note.getAuthor().getId(),
                        note.getAuthor().getUsername()
                ))
                .toList();
    }

    @Transactional
    public void deleteAnyNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new RuntimeException("Заметка не найдена");
        }
        noteRepository.deleteById(id);
    }
}