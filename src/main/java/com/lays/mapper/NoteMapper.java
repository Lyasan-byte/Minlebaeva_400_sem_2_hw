package com.lays.mapper;

import com.lays.dto.NoteDTO;
import com.lays.model.Note;
import com.lays.model.User;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteDTO toDTO(Note note) {
        if (note == null) {
            return null;
        }

        return new NoteDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.isPublic()
        );
    }

    // Добавьте этот метод
    public Note toEntity(NoteDTO dto, User author) {
        if (dto == null) {
            return null;
        }

        Note note = new Note();
        note.setId(dto.getId());
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setPublic(dto.isPublic());
        note.setAuthor(author);

        return note;
    }

    // Добавьте метод для обновления существующей заметки
    public void updateEntity(NoteDTO dto, Note note) {
        if (dto == null || note == null) {
            return;
        }

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setPublic(dto.isPublic());
    }
}