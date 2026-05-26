package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/roll/{rollNumber}")
    public ResponseEntity<Student> getStudentByRollNumber(
            @PathVariable String rollNumber) {
        return studentService.getStudentByRollNumber(rollNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Student>> getStudentsByDepartment(
            @PathVariable String department) {
        return ResponseEntity.ok(studentService.getStudentsByDepartment(department));
    }

    @GetMapping("/department/{department}/semester/{semester}")
    public ResponseEntity<List<Student>> getStudentsByDepartmentAndSemester(
            @PathVariable String department,
            @PathVariable Integer semester) {
        return ResponseEntity.ok(
            studentService.getStudentsByDepartmentAndSemester(department, semester));
    }

    @PostMapping
    public ResponseEntity<Student> createStudent( @Valid@RequestBody Student student) {
        Student saved = studentService.saveStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @Valid@RequestBody Student student) {
        return ResponseEntity.ok(studentService.updateStudent(id, student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully!");
    }
}