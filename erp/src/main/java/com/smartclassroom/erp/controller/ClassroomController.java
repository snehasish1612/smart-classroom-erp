package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Classroom;
import com.smartclassroom.erp.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Classroom>> getAll() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getById(@PathVariable Long id) {
        return classroomService.getClassroomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Classroom>> getByStatus(@PathVariable Classroom.Status status) {
        return ResponseEntity.ok(classroomService.getByStatus(status));
    }

    // GET BY BUILDING
    @GetMapping("/building/{building}")
    public ResponseEntity<List<Classroom>> getByBuilding(@PathVariable String building) {
        return ResponseEntity.ok(classroomService.getByBuilding(building));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Classroom> create(@RequestBody Classroom classroom) {
        return ResponseEntity.ok(classroomService.createClassroom(classroom));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Classroom> update(@PathVariable Long id,
                                             @RequestBody Classroom classroom) {
        return ResponseEntity.ok(classroomService.updateClassroom(id, classroom));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.ok("Classroom deleted successfully!");
    }
}