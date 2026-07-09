package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.dto.NoteGenerationRequest;
import com.smartclassroom.erp.entity.Note;
import com.smartclassroom.erp.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getNotes(@RequestParam(required = false) String subject) {
        if (subject == null || subject.isBlank()) {
            return ResponseEntity.ok(noteService.getAllNotes());
        }

        return ResponseEntity.ok(noteService.getNotesBySubject(subject));
    }

    @PostMapping("/generate")
    public ResponseEntity<Note> generateNote(@Valid @RequestBody NoteGenerationRequest request) {
        return ResponseEntity.ok(noteService.generateNote(request));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<String> downloadNote(@PathVariable Long id) {
        Note note = noteService.getNote(id);
        String filename = note.getSubject().replaceAll("[^a-zA-Z0-9_-]", "_")
            + "-" + note.getTopic().replaceAll("[^a-zA-Z0-9_-]", "_") + ".txt";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.TEXT_PLAIN)
            .body(note.getContent());
    }
}
