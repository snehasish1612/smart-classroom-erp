package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Stream;
import com.smartclassroom.erp.entity.Timetable;
import com.smartclassroom.erp.entity.Timetable.Day;
import com.smartclassroom.erp.repository.FacultyRepository;
import com.smartclassroom.erp.repository.StreamRepository;
import com.smartclassroom.erp.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TimetableService {

@Autowired
private TimetableRepository timetableRepository;

@Autowired
private StreamRepository streamRepository;

@Autowired
private FacultyRepository facultyRepository;

// Get all timetables
public List<Timetable> getAllTimetables() {
    return timetableRepository.findAll();
}

// Get timetable by id
public Optional<Timetable> getTimetableById(Long id) {
    return timetableRepository.findById(id);
}

// Get timetable by stream
public List<Timetable> getTimetableByStream(Long streamId) {
    Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> new RuntimeException("Stream not found!"));
    return timetableRepository.findByStream(stream);
}

// Get timetable by stream and semester
public List<Timetable> getTimetableByStreamAndSemester(
        Long streamId, Integer semester) {

    Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> new RuntimeException("Stream not found!"));

    return timetableRepository.findByStreamAndSemester(
            stream, semester);
}

// Get timetable by stream, semester and day
public List<Timetable> getTimetableByStreamSemesterAndDay(
        Long streamId, Integer semester, Day day) {

    Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> new RuntimeException("Stream not found!"));

    return timetableRepository.findByStreamAndSemesterAndDay(
            stream, semester, day);
}

// Get timetable by faculty
public List<Timetable> getTimetableByFaculty(Long facultyId) {

    Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RuntimeException("Faculty not found!"));

    return timetableRepository.findByFaculty(faculty);
}

// Get timetable by faculty and day
public List<Timetable> getTimetableByFacultyAndDay(
        Long facultyId, Day day) {

    Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RuntimeException("Faculty not found!"));

    return timetableRepository.findByFacultyAndDay(faculty, day);
}

// Create timetable entry
public Timetable createTimetable(Long streamId, Timetable timetable) {

    // Step 1: Find stream
    Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> new RuntimeException("Stream not found!"));

    // Step 2: Check classroom clash
    if (timetableRepository.existsClassroomConflict(
            timetable.getClassroom(),
            timetable.getDay(),
            timetable.getStartTime(),
            timetable.getEndTime())) {

        throw new RuntimeException(
                "Classroom already booked at this time!");
    }

    // Step 3: Check faculty clash
    if (timetableRepository.existsFacultyConflict(
            timetable.getFaculty(),
            timetable.getDay(),
            timetable.getStartTime(),
            timetable.getEndTime())) {

        throw new RuntimeException(
                "Faculty already has a class at this time!");
    }

    // Step 4: Link stream
    timetable.setStream(stream);

    return timetableRepository.save(timetable);
}

// Update timetable
public Timetable updateTimetable(Long id,
                                 Timetable updatedTimetable) {

    Timetable existing = timetableRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Timetable not found!"));

    existing.setSubject(updatedTimetable.getSubject());
    existing.setFaculty(updatedTimetable.getFaculty());
    existing.setClassroom(updatedTimetable.getClassroom());
    existing.setDay(updatedTimetable.getDay());
    existing.setStartTime(updatedTimetable.getStartTime());
    existing.setEndTime(updatedTimetable.getEndTime());
    existing.setSemester(updatedTimetable.getSemester());

    return timetableRepository.save(existing);
}

// Delete timetable
public void deleteTimetable(Long id) {

    if (!timetableRepository.existsById(id)) {
        throw new RuntimeException("Timetable not found!");
    }

    timetableRepository.deleteById(id);
}


}
