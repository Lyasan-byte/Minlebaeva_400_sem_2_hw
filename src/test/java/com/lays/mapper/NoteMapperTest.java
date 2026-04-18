package com.lays.mapper;

import com.lays.dto.NoteDTO;
import com.lays.model.Note;
import com.lays.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteMapperTest {

    private final NoteMapper noteMapper = new NoteMapper();

    @Test
    void toDTO_mapsNoteFields() {
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Title");
        note.setContent("Content");
        note.setPublic(true);

        NoteDTO result = noteMapper.toDTO(note);

        assertEquals(1L, result.getId());
        assertEquals("Title", result.getTitle());
        assertEquals("Content", result.getContent());
        assertTrue(result.isPublic());
    }

    @Test
    void toDTO_returnsNullForNullNote() {
        assertNull(noteMapper.toDTO(null));
    }

    @Test
    void toEntity_mapsDtoAndAuthor() {
        NoteDTO dto = new NoteDTO(2L, "Title", "Body", false);
        User author = new User();
        author.setUsername("alice");

        Note result = noteMapper.toEntity(dto, author);

        assertEquals(2L, result.getId());
        assertEquals("Title", result.getTitle());
        assertEquals("Body", result.getContent());
        assertFalse(result.isPublic());
        assertSame(author, result.getAuthor());
    }

    @Test
    void toEntity_returnsNullForNullDto() {
        assertNull(noteMapper.toEntity(null, new User()));
    }

    @Test
    void updateEntity_updatesMutableFields() {
        NoteDTO dto = new NoteDTO(3L, "Updated", "New content", true);
        Note note = new Note();
        note.setTitle("Old");
        note.setContent("Old content");
        note.setPublic(false);

        noteMapper.updateEntity(dto, note);

        assertEquals("Updated", note.getTitle());
        assertEquals("New content", note.getContent());
        assertTrue(note.isPublic());
    }

    @Test
    void updateEntity_ignoresNullArguments() {
        Note note = new Note();
        note.setTitle("Old");
        note.setContent("Old content");
        note.setPublic(false);

        noteMapper.updateEntity(null, note);
        assertEquals("Old", note.getTitle());

        noteMapper.updateEntity(new NoteDTO(1L, "New", "Body", true), null);
    }
}
