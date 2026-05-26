package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Timetable;
import com.smartclassroom.erp.entity.Timetable.Day;
import com.smartclassroom.erp.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Timetable>> getAll() {
        return ResponseEntity.ok(timetableService.getAllTimetables());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Timetable> getById(@PathVariable Long id) {
        return timetableService.getTimetableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET BY DAY
    @GetMapping("/day/{day}")
    public ResponseEntity<List<Timetable>> getByDay(@PathVariable Day day) {
        return ResponseEntity.ok(timetableService.getTimetableByDay(day));
    }

    // GET BY DEPARTMENT
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Timetable>> getByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(timetableService.getTimetableByDepartment(department));
    }

    // GET BY DEPARTMENT + DAY
    @GetMapping("/department/{department}/day/{day}")
    public ResponseEntity<List<Timetable>> getByDepartmentAndDay(
            @PathVariable String department,
            @PathVariable Day day) {

        return ResponseEntity.ok(
                timetableService.getTimetableByDepartmentAndDay(department, day));
    }

    // GET BY FACULTY
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Timetable>> getByFaculty(@PathVariable Long facultyId) {
        return ResponseEntity.ok(timetableService.getTimetableByFaculty(facultyId));
    }

    // POST
    @PostMapping
    public ResponseEntity<Timetable> create(@RequestBody Timetable timetable) {
        return ResponseEntity.ok(timetableService.createTimetable(timetable));
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Timetable> update(
            @PathVariable Long id,
            @RequestBody Timetable timetable) {
        return ResponseEntity.ok(timetableService.updateTimetable(id, timetable));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        timetableService.deleteTimetable(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}