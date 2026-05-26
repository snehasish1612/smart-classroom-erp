package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.service.FacultyService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@CrossOrigin(origins = "*")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    // Get all faculty
    @GetMapping
    public ResponseEntity<List<Faculty>> getAllFaculty() {
        return ResponseEntity.ok(facultyService.getAllFaculty());
    }

    // Get faculty by ID
    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFacultyById(
            @PathVariable Long id) {

        return facultyService.getFacultyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET faculty by email
    @GetMapping("/email/{email}")
    public ResponseEntity<Faculty> getFacultyByEmail(
            @PathVariable String email) {
        return facultyService.getFacultyByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get faculty by department
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Faculty>> getByDepartment(
            @PathVariable String department) {

        return ResponseEntity.ok(
                facultyService.getFacultyByDepartment(department));
    }

    // Get faculty by subject
    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<Faculty>> getBySubject(
            @PathVariable String subject) {

        return ResponseEntity.ok(
                facultyService.getFacultyBySubject(subject));
    }

    //POST-  Create  new faculty
    @PostMapping
    public ResponseEntity<Faculty> createFaculty(
            @Valid @RequestBody Faculty faculty) {

        Faculty savedFaculty =
                facultyService.saveFaculty(faculty);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedFaculty);
    }

    // PUT- Update faculty
    @PutMapping("/{id}")
    public ResponseEntity<Faculty> updateFaculty(
            @PathVariable Long id,
            @Valid @RequestBody Faculty faculty) {

        return ResponseEntity.ok(
                facultyService.updateFaculty(id, faculty));
    }

    // Delete faculty
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFaculty(
            @PathVariable Long id) {

        facultyService.deleteFaculty(id);

        return ResponseEntity.ok(
                "Faculty deleted successfully!");
    }
}