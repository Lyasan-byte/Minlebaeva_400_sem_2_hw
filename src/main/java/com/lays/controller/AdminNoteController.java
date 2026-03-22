package com.lays.controller;

import com.lays.dto.AdminNoteDTO;
import com.lays.service.NoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/notes")
public class AdminNoteController {

    private final NoteService noteService;

    public AdminNoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<AdminNoteDTO> getAllNotes() {
        return noteService.getAllNotesForAdmin();
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable Long id) {
        noteService.deleteAnyNote(id);
        return "Заметка удалена";
    }
}