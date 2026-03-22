package com.lays.mapper;

import com.lays.dto.NoteDTO;
import com.lays.model.Note;
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
}