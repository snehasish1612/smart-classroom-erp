package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Attendance;
import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Get all attendance by student
    List<Attendance> findByStudent(Student student);

    // Get all attendance by section
    List<Attendance> findBySection(Section section);

    // Get attendance by section and date
    List<Attendance> findBySectionAndDate(Section section, LocalDate date);

    // Get attendance by section and subject
    List<Attendance> findBySectionAndSubject(Section section, String subject);

    // Get attendance by student and subject
    List<Attendance> findByStudentAndSubject(Student student, String subject);

    // Get attendance by date
    List<Attendance> findByDate(LocalDate date);

    // Check if attendance already exists
    Boolean existsByStudentAndSubjectAndDate(
        Student student, String subject, LocalDate date);

    // Count present days for student in subject
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = ?1 " +
           "AND a.subject = ?2 AND a.status = 'PRESENT'")
    Long countPresentByStudentAndSubject(Student student, String subject);

    // Count total days for student in subject
    Long countByStudentAndSubject(Student student, String subject);
}