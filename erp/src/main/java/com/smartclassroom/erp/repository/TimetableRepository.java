package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Classroom;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Timetable;
import com.smartclassroom.erp.entity.Timetable.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // ---------------- BASIC QUERIES ----------------

    List<Timetable> findByDay(Day day);

    List<Timetable> findByDepartment(String department);

    List<Timetable> findByDepartmentAndSemester(String department, Integer semester);

    List<Timetable> findByDepartmentAndDay(String department, Day day);

    List<Timetable> findByFaculty(Faculty faculty);

    List<Timetable> findByFacultyAndDay(Faculty faculty, Day day);

    List<Timetable> findByClassroomAndDay(Classroom classroom, Day day);

    // ---------------- REAL ERP CONFLICT LOGIC ----------------

    // Classroom TIME OVERLAP check (IMPORTANT FIX)
    @Query("""
    SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
    FROM Timetable t
    WHERE t.classroom = :classroom
    AND t.day = :day
    AND (
        :startTime < t.endTime
        AND :endTime > t.startTime
    )
    """)
    boolean existsClassroomConflict(
            @Param("classroom") Classroom classroom,
            @Param("day") Day day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Faculty TIME OVERLAP check
    @Query("""
    SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
    FROM Timetable t
    WHERE t.faculty = :faculty
    AND t.day = :day
    AND (
        :startTime < t.endTime
        AND :endTime > t.startTime
    )
    """)
    boolean existsFacultyConflict(
            @Param("faculty") Faculty faculty,
            @Param("day") Day day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}