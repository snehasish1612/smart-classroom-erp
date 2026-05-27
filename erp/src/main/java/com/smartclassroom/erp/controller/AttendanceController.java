package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Attendance;
import com.smartclassroom.erp.entity.Attendance.Status;
import com.smartclassroom.erp.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // GET all attendance
    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    // GET attendance by student id
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getAttendanceByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceByStudentId(studentId));
    }

    // GET attendance by student and subject
    @GetMapping("/student/{studentId}/subject/{subject}")
    public ResponseEntity<List<Attendance>> getAttendanceByStudentAndSubject(
            @PathVariable Long studentId,
            @PathVariable String subject) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceByStudentAndSubject(
                studentId, subject));
    }

    // GET attendance by date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceByDate(date));
    }

    // GET attendance by subject and date
    @GetMapping("/subject/{subject}/date/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceBySubjectAndDate(
            @PathVariable String subject,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceBySubjectAndDate(subject, date));
    }

    // GET attendance percentage
    @GetMapping("/percentage/student/{studentId}/subject/{subject}")
    public ResponseEntity<Double> getAttendancePercentage(
            @PathVariable Long studentId,
            @PathVariable String subject) {
        double percentage = attendanceService
            .getAttendancePercentage(studentId, subject);
        return ResponseEntity.ok(percentage);
    }

    // POST - mark attendance
    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(
            @RequestParam Long studentId,
            @RequestParam Long facultyId,
            @RequestParam String subject,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam Status status) {
        Attendance attendance = attendanceService.markAttendance(
            studentId, facultyId, subject, date, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
    }

    // PUT - update attendance status
    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable Long id,
            @RequestParam Status status) {
        Attendance updated = attendanceService.updateAttendance(id, status);
        return ResponseEntity.ok(updated);
    }

    // DELETE - delete attendance
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok("Attendance deleted successfully!");
    }
}