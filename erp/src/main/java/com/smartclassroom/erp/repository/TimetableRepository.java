package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Stream;
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

    // Get timetable by stream
    List<Timetable> findByStream(Stream stream);

    // Get timetable by stream and semester
    List<Timetable> findByStreamAndSemester(
            Stream stream,
            Integer semester
    );

    // Get timetable by stream, semester and day
    List<Timetable> findByStreamAndSemesterAndDay(
            Stream stream,
            Integer semester,
            Day day
    );

    // Get timetable by faculty
    List<Timetable> findByFaculty(Faculty faculty);

    // Get timetable by faculty and day
    List<Timetable> findByFacultyAndDay(
            Faculty faculty,
            Day day
    );

    // Classroom clash check
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
            @Param("classroom") String classroom,
            @Param("day") Day day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    // Faculty clash check
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