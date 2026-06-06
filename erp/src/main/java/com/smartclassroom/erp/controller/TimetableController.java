package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Timetable;
import com.smartclassroom.erp.entity.Timetable.Day;
import com.smartclassroom.erp.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    // GET all timetables
    @GetMapping
    public ResponseEntity<List<Timetable>> getAllTimetables() {
        return ResponseEntity.ok(timetableService.getAllTimetables());
    }

    // GET timetable by id
    @GetMapping("/{id}")
    public ResponseEntity<Timetable> getTimetableById(
            @PathVariable Long id) {
        return timetableService.getTimetableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET timetable by stream
    @GetMapping("/stream/{streamId}")
    public ResponseEntity<List<Timetable>> getTimetableByStream(
            @PathVariable Long streamId) {
        return ResponseEntity.ok(
            timetableService.getTimetableByStream(streamId));
    }

    // GET timetable by stream and semester
    @GetMapping("/stream/{streamId}/semester/{semester}")
    public ResponseEntity<List<Timetable>> getTimetableByStreamAndSemester(
            @PathVariable Long streamId,
            @PathVariable Integer semester) {
        return ResponseEntity.ok(
            timetableService.getTimetableByStreamAndSemester(
                streamId, semester));
    }

    // GET timetable by stream, semester and day
    @GetMapping("/stream/{streamId}/semester/{semester}/day/{day}")
    public ResponseEntity<List<Timetable>> getTimetableByStreamSemesterAndDay(
            @PathVariable Long streamId,
            @PathVariable Integer semester,
            @PathVariable Day day) {
        return ResponseEntity.ok(
            timetableService.getTimetableByStreamSemesterAndDay(
                streamId, semester, day));
    }

    // GET timetable by faculty
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Timetable>> getTimetableByFaculty(
            @PathVariable Long facultyId) {
        return ResponseEntity.ok(
            timetableService.getTimetableByFaculty(facultyId));
    }

    // GET timetable by faculty and day
    @GetMapping("/faculty/{facultyId}/day/{day}")
    public ResponseEntity<List<Timetable>> getTimetableByFacultyAndDay(
            @PathVariable Long facultyId,
            @PathVariable Day day) {
        return ResponseEntity.ok(
            timetableService.getTimetableByFacultyAndDay(
                facultyId, day));
    }

    // POST - create timetable under stream
    @PostMapping("/stream/{streamId}")
    public ResponseEntity<Timetable> createTimetable(
            @PathVariable Long streamId,
            @RequestBody Timetable timetable) {
        Timetable saved = timetableService
            .createTimetable(streamId, timetable);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT - update timetable
    @PutMapping("/{id}")
    public ResponseEntity<Timetable> updateTimetable(
            @PathVariable Long id,
            @RequestBody Timetable timetable) {
        Timetable updated = timetableService
            .updateTimetable(id, timetable);
        return ResponseEntity.ok(updated);
    }

    // DELETE - delete timetable
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTimetable(
            @PathVariable Long id) {
        timetableService.deleteTimetable(id);
        return ResponseEntity.ok("Timetable deleted successfully!");
    }
}