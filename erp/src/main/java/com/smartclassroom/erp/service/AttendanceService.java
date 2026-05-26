package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Attendance;
import com.smartclassroom.erp.entity.Attendance.Status;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.repository.AttendanceRepository;
import com.smartclassroom.erp.repository.FacultyRepository;
import com.smartclassroom.erp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    // Get all attendance
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    // Get attendance by student id
    public List<Attendance> getAttendanceByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found!"));
        return attendanceRepository.findByStudent(student);
    }

    // Get attendance by student and subject
    public List<Attendance> getAttendanceByStudentAndSubject(
            Long studentId, String subject) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found!"));
        return attendanceRepository.findByStudentAndSubject(student, subject);
    }

    // Get attendance by date
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    // Get attendance by subject and date
    public List<Attendance> getAttendanceBySubjectAndDate(
            String subject, LocalDate date) {
        return attendanceRepository.findBySubjectAndDate(subject, date);
    }

    // Mark attendance
    public Attendance markAttendance(
            Long studentId, Long facultyId,
            String subject, LocalDate date, Status status) {

        // Step 1: Find student
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found!"));

        // Step 2: Find faculty
        Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RuntimeException("Faculty not found!"));

        // Step 3: Business logic
        // Check if attendance already marked
        if (attendanceRepository.existsByStudentAndSubjectAndDate(
                student, subject, date)) {
            throw new RuntimeException(
                "Attendance already marked for this student on this date!");
        }

        // Step 4: Create attendance record
        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setFaculty(faculty);
        attendance.setSubject(subject);
        attendance.setDate(date);
        attendance.setStatus(status);

        // Step 5: Save and return
        return attendanceRepository.save(attendance);
    }

    // Calculate attendance percentage
    public double getAttendancePercentage(Long studentId, String subject) {

        // Step 1: Find student
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found!"));

        // Step 2: Count present days
        Long presentDays = attendanceRepository
            .countPresentByStudentAndSubject(student, subject);

        // Step 3: Count total days
        Long totalDays = attendanceRepository
            .countByStudentAndSubject(student, subject);

        // Step 4: Handle zero division
        if (totalDays == 0) {
            return 0.0;
        }

        // Step 5: Calculate and return percentage
        return ((double) presentDays / totalDays) * 100;
    }

    // Update attendance
    public Attendance updateAttendance(Long id, Status status) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance not found!"));
        attendance.setStatus(status);
        return attendanceRepository.save(attendance);
    }

    // Delete attendance
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new RuntimeException("Attendance not found!");
        }
        attendanceRepository.deleteById(id);
    }
}