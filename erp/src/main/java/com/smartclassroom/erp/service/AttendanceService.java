package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Attendance;
import com.smartclassroom.erp.entity.Attendance.Status;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.repository.AttendanceRepository;
import com.smartclassroom.erp.repository.FacultyRepository;
import com.smartclassroom.erp.repository.SectionRepository;
import com.smartclassroom.erp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private SectionRepository sectionRepository;

    // Get all attendance
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    // Get attendance by student
    public List<Attendance> getAttendanceByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found!"));
        return attendanceRepository.findByStudent(student);
    }

    // Get attendance by section
    public List<Attendance> getAttendanceBySection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found!"));
        return attendanceRepository.findBySection(section);
    }

    // Get attendance by section and date
    public List<Attendance> getAttendanceBySectionAndDate(
            Long sectionId, LocalDate date) {
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found!"));
        return attendanceRepository.findBySectionAndDate(section, date);
    }

    // Get attendance by section and subject
    public List<Attendance> getAttendanceBySectionAndSubject(
            Long sectionId, String subject) {
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found!"));
        return attendanceRepository.findBySectionAndSubject(section, subject);
    }

    // Mark attendance
    public Attendance markAttendance(
            Long studentId, Long facultyId,
            Long sectionId, String subject,
            LocalDate date, Status status) {
        return markAttendance(studentId, facultyId, sectionId, subject, date, status, null, null);
    }

    public Attendance markAttendance(
            Long studentId, Long facultyId,
            Long sectionId, String subject,
            LocalDate date, Status status,
            Double latitude, Double longitude) {

        // Step 1: Find all required entities
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found!"));
        Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RuntimeException("Faculty not found!"));
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found!"));

        // Step 2: Check duplicate
        if (attendanceRepository.existsByStudentAndSubjectAndDate(
                student, subject, date)) {
            throw new RuntimeException(
                "Attendance already marked for this student today!");
        }

        // Step 3: Create attendance
        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setFaculty(faculty);
        attendance.setSection(section);
        attendance.setSubject(subject);
        attendance.setDate(date);
        attendance.setStatus(status);
        attendance.setLatitude(latitude);
        attendance.setLongitude(longitude);

        return attendanceRepository.save(attendance);
    }

    public double calculateDistance(
            double sourceLatitude,
            double sourceLongitude,
            double targetLatitude,
            double targetLongitude) {
        double latitudeDistance = Math.toRadians(targetLatitude - sourceLatitude);
        double longitudeDistance = Math.toRadians(targetLongitude - sourceLongitude);
        double sourceLatitudeRadians = Math.toRadians(sourceLatitude);
        double targetLatitudeRadians = Math.toRadians(targetLatitude);

        double haversine = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2)
            + Math.cos(sourceLatitudeRadians)
            * Math.cos(targetLatitudeRadians)
            * Math.sin(longitudeDistance / 2)
            * Math.sin(longitudeDistance / 2);
        double angularDistance = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

        return EARTH_RADIUS_METERS * angularDistance;
    }

    // Calculate attendance percentage
    public double getAttendancePercentage(Long studentId, String subject) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found!"));

        Long presentDays = attendanceRepository
            .countPresentByStudentAndSubject(student, subject);
        Long totalDays = attendanceRepository
            .countByStudentAndSubject(student, subject);

        if (totalDays == 0) return 0.0;

        return ((double) presentDays / totalDays) * 100;
    }

    // Update attendance status
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
