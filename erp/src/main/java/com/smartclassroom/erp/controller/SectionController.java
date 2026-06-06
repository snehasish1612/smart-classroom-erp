package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    // GET all sections
    @GetMapping
    public ResponseEntity<List<Section>> getAllSections() {
        return ResponseEntity.ok(sectionService.getAllSections());
    }

    // GET section by id
    @GetMapping("/{id}")
    public ResponseEntity<Section> getSectionById(
            @PathVariable Long id) {
        return sectionService.getSectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET sections by stream
    @GetMapping("/stream/{streamId}")
    public ResponseEntity<List<Section>> getSectionsByStream(
            @PathVariable Long streamId) {
        return ResponseEntity.ok(
            sectionService.getSectionsByStream(streamId));
    }

    // GET sections by stream and semester
    @GetMapping("/stream/{streamId}/semester/{semester}")
    public ResponseEntity<List<Section>> getSectionsByStreamAndSemester(
            @PathVariable Long streamId,
            @PathVariable Integer semester) {
        return ResponseEntity.ok(
            sectionService.getSectionsByStreamAndSemester(
                streamId, semester));
    }

    // POST - create section under a stream
    @PostMapping("/stream/{streamId}")
    public ResponseEntity<Section> createSection(
            @PathVariable Long streamId,
            @RequestBody Section section) {
        Section saved = sectionService.createSection(streamId, section);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT - update section
    @PutMapping("/{id}")
    public ResponseEntity<Section> updateSection(
            @PathVariable Long id,
            @RequestBody Section section) {
        Section updated = sectionService.updateSection(id, section);
        return ResponseEntity.ok(updated);
    }

    // DELETE - delete section
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSection(
            @PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.ok("Section deleted successfully!");
    }
}