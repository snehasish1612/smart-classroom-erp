package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.Attendance;
import com.smartclassroom.erp.entity.Attendance.Status;
import com.smartclassroom.erp.dto.AttendanceLocationRequest;
import com.smartclassroom.erp.service.AttendanceService;
import jakarta.validation.Valid;
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

    private static final double CAMPUS_LATITUDE = 22.7237;
    private static final double CAMPUS_LONGITUDE = 88.3792;
    private static final double CAMPUS_RADIUS_METERS = 150;

    @Autowired
    private AttendanceService attendanceService;

    // GET all attendance
    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    // GET attendance by student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getAttendanceByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceByStudentId(studentId));
    }

    // GET attendance by section
    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<Attendance>> getAttendanceBySection(
            @PathVariable Long sectionId) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceBySection(sectionId));
    }

    // GET attendance by section and date
    @GetMapping("/section/{sectionId}/date/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceBySectionAndDate(
            @PathVariable Long sectionId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceBySectionAndDate(
                sectionId, date));
    }

    // GET attendance by section and subject
    @GetMapping("/section/{sectionId}/subject/{subject}")
    public ResponseEntity<List<Attendance>> getAttendanceBySectionAndSubject(
            @PathVariable Long sectionId,
            @PathVariable String subject) {
        return ResponseEntity.ok(
            attendanceService.getAttendanceBySectionAndSubject(
                sectionId, subject));
    }

    // GET attendance percentage
    @GetMapping("/percentage/student/{studentId}/subject/{subject}")
    public ResponseEntity<Double> getAttendancePercentage(
            @PathVariable Long studentId,
            @PathVariable String subject) {
        return ResponseEntity.ok(
            attendanceService.getAttendancePercentage(
                studentId, subject));
    }

    // POST - mark attendance
    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(
            @RequestParam Long studentId,
            @RequestParam Long facultyId,
            @RequestParam Long sectionId,
            @RequestParam String subject,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam Status status) {
        Attendance attendance = attendanceService.markAttendance(
            studentId, facultyId, sectionId, subject, date, status);
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
    }

    @PostMapping("/mark-by-location")
    public ResponseEntity<?> markAttendanceByLocation(
            @Valid @RequestBody AttendanceLocationRequest request) {
        double distance = attendanceService.calculateDistance(
            request.getLatitude(),
            request.getLongitude(),
            CAMPUS_LATITUDE,
            CAMPUS_LONGITUDE);

        if (distance > CAMPUS_RADIUS_METERS) {
            return ResponseEntity.badRequest()
                .body("You are outside college campus.");
        }

        Attendance attendance = attendanceService.markAttendance(
            request.getStudentId(),
            request.getFacultyId(),
            request.getSectionId(),
            request.getSubject(),
            LocalDate.now(),
            Status.PRESENT,
            request.getLatitude(),
            request.getLongitude());
        return ResponseEntity.status(HttpStatus.CREATED).body(attendance);
    }

    // PUT - update attendance status
    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable Long id,
            @RequestParam Status status) {
        return ResponseEntity.ok(
            attendanceService.updateAttendance(id, status));
    }

    // DELETE - delete attendance
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttendance(
            @PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok("Attendance deleted successfully!");
    }
}
