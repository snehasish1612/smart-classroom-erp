package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Attendance;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    // Get all attendance by student
    List<Attendance> findByStudent(Student student);

    // Get all attendance by faculty
    List<Attendance> findByFaculty(Faculty faculty);

    // Get attendance by student and subject
    List<Attendance> findByStudentAndSubject(
            Student student,
            String subject);

    // Get attendance by student and date
    List<Attendance> findByStudentAndDate(
            Student student,
            LocalDate date);

    // Get attendance by date
    List<Attendance> findByDate(LocalDate date);

    // Get attendance by subject and date
    List<Attendance> findBySubjectAndDate(
            String subject,
            LocalDate date);

    // Check duplicate attendance
    boolean existsByStudentAndSubjectAndDate(
            Student student,
            String subject,
            LocalDate date);

    // Count present days
    @Query("""
        SELECT COUNT(a)
        FROM Attendance a
        WHERE a.student = ?1
        AND a.subject = ?2
        AND a.status = 'PRESENT'
    """)
    Long countPresentByStudentAndSubject(
            Student student,
            String subject);

    // // Count total days for a student in a subject
    Long countByStudentAndSubject(
            Student student,
            String subject);
}