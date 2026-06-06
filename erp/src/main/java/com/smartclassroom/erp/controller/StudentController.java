package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // GET all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // GET student by id
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET student by roll number
    @GetMapping("/roll/{rollNumber}")
    public ResponseEntity<Student> getStudentByRollNumber(
            @PathVariable String rollNumber) {
        return studentService.getStudentByRollNumber(rollNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET students by section
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<Student>> getStudentsBySection(
            @PathVariable Long sectionId) {
        return ResponseEntity.ok(
            studentService.getStudentsBySection(sectionId));
    }

    // POST - create student under a section
    @PostMapping("/section/{sectionId}")
    public ResponseEntity<Student> createStudent(
            @PathVariable Long sectionId,
            @RequestBody Student student) {
        Student saved = studentService.saveStudent(sectionId, student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT - update student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @RequestBody Student student) {
        Student updated = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updated);
    }

    // DELETE - delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(
            @PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully!");
    }
}