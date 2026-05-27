package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.*;
import com.smartclassroom.erp.entity.Timetable.Day;
import com.smartclassroom.erp.repository.*;
import com.smartclassroom.erp.config.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TimetableService {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    // GET ALL
    public List<Timetable> getAllTimetables() {
        return timetableRepository.findAll();
    }

    // GET BY ID
    public Optional<Timetable> getTimetableById(Long id) {
        return timetableRepository.findById(id);
    }

    // GET BY DAY
    public List<Timetable> getTimetableByDay(Day day) {
        return timetableRepository.findByDay(day);
    }

    // GET BY DEPARTMENT
    public List<Timetable> getTimetableByDepartment(String department) {
        return timetableRepository.findByDepartment(department);
    }

    // GET BY DEPARTMENT + SEMESTER
    public List<Timetable> getTimetableByDepartmentAndSemester(
            String department, Integer semester) {
        return timetableRepository.findByDepartmentAndSemester(department, semester);
    }

    // GET BY DEPARTMENT + DAY (✔ FIXED METHOD NAME)
    public List<Timetable> getTimetableByDepartmentAndDay(
            String department, Day day) {
        return timetableRepository.findByDepartmentAndDay(department, day);
    }

    // GET BY FACULTY
    public List<Timetable> getTimetableByFaculty(Long facultyId) {

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        return timetableRepository.findByFaculty(faculty);
    }

    // GET BY FACULTY + DAY
    public List<Timetable> getTimetableByFacultyAndDay(Long facultyId, Day day) {

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        return timetableRepository.findByFacultyAndDay(faculty, day);
    }

   public Timetable createTimetable(Timetable timetable) {

    // Classroom conflict (FIXED)
    if (timetableRepository.existsClassroomConflict(
            timetable.getClassroom(),
            timetable.getDay(),
            timetable.getStartTime(),
            timetable.getEndTime())) {

        throw new RuntimeException("Classroom already booked at this time!");
    }

    // Faculty conflict (FIXED)
    if (timetableRepository.existsFacultyConflict(
            timetable.getFaculty(),
            timetable.getDay(),
            timetable.getStartTime(),
            timetable.getEndTime())) {

        throw new RuntimeException("Faculty already has class at this time!");
    }

    return timetableRepository.save(timetable);
}
    // UPDATE
    public Timetable updateTimetable(Long id, Timetable updated) {

        Timetable existing = timetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable not found"));

        existing.setSubject(updated.getSubject());
        existing.setFaculty(updated.getFaculty());
        existing.setClassroom(updated.getClassroom());
        existing.setDay(updated.getDay());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setDepartment(updated.getDepartment());
        existing.setSemester(updated.getSemester());

        return timetableRepository.save(existing);
    }

    // DELETE
    public void deleteTimetable(Long id) {

        if (!timetableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Timetable not found");
        }

        timetableRepository.deleteById(id);
    }
}