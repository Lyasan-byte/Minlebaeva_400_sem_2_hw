package com.lays.controller;

import com.lays.dto.NoteDTO;
import com.lays.model.Note;
import com.lays.service.CustomUserDetails;
import com.lays.service.NoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public String getMyNotes(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             Model model) {
        List<Note> notes;

        if (keyword != null && !keyword.isBlank()) {
            notes = noteService.searchNotesByTitle(keyword).stream()
                    .filter(note -> note.getAuthor().getUsername().equals(userDetails.getUsername()))
                    .toList();
        } else {
            notes = noteService.getMyNotes(userDetails.getUsername());
        }

        model.addAttribute("notes", notes);
        model.addAttribute("keyword", keyword);
        return "notes";
    }

    @GetMapping("/public")
    public String getPublicNotes(@RequestParam(value = "keyword", required = false) String keyword,
                                 Model model) {
        List<Note> notes;

        if (keyword != null && !keyword.isBlank()) {
            notes = noteService.searchNotesByTitle(keyword).stream()
                    .filter(Note::isPublic)
                    .toList();
        } else {
            notes = noteService.getPublicNotes();
        }

        model.addAttribute("notes", notes);
        model.addAttribute("keyword", keyword);
        return "public_notes";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("note", new NoteDTO());
        model.addAttribute("formTitle", "Создать заметку");
        model.addAttribute("formAction", "/notes/create");
        return "note_form";
    }

    @PostMapping("/create")
    public String createNote(@ModelAttribute("note") NoteDTO noteDTO,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        try {
            noteService.createNote(noteDTO, userDetails.getUsername());
            return "redirect:/notes";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("note", noteDTO);
            model.addAttribute("formTitle", "Создать заметку");
            model.addAttribute("formAction", "/notes/create");
            return "note_form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable(name = "id") Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        try {
            model.addAttribute("note", noteService.getOwnedNoteForEdit(id, userDetails.getUsername()));
            model.addAttribute("formTitle", "Редактировать заметку");
            model.addAttribute("formAction", "/notes/" + id + "/edit");
            return "note_form";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{id}/edit")
    public String editNote(@PathVariable(name = "id") Long id,
                           @ModelAttribute("note") NoteDTO noteDTO,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        try {
            noteService.updateNote(id, noteDTO, userDetails.getUsername());
            return "redirect:/notes";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("note", noteDTO);
            model.addAttribute("formTitle", "Редактировать заметку");
            model.addAttribute("formAction", "/notes/" + id + "/edit");
            return "note_form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable(name = "id") Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        try {
            noteService.deleteNote(id, userDetails.getUsername());
            return "redirect:/notes";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}