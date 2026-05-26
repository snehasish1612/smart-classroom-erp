package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Classroom;
import com.smartclassroom.erp.repository.ClassroomRepository;
import com.smartclassroom.erp.config.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepository;

    // GET ALL
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    // GET BY ID
    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    // GET BY STATUS
    public List<Classroom> getByStatus(Classroom.Status status) {
        return classroomRepository.findByStatus(status);
    }

    // GET BY BUILDING
    public List<Classroom> getByBuilding(String building) {
        return classroomRepository.findByBuilding(building);
    }

    // CREATE
    public Classroom createClassroom(Classroom classroom) {

        if (classroomRepository.existsByRoomNumber(classroom.getRoomNumber())) {
            throw new RuntimeException("Room number already exists!");
        }

        return classroomRepository.save(classroom);
    }

    // UPDATE
    public Classroom updateClassroom(Long id, Classroom updated) {

        Classroom existing = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found!"));

        existing.setRoomNumber(updated.getRoomNumber());
        existing.setBuilding(updated.getBuilding());
        existing.setCapacity(updated.getCapacity());
        existing.setStatus(updated.getStatus());

        return classroomRepository.save(existing);
    }

    // DELETE
    public void deleteClassroom(Long id) {

        if (!classroomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Classroom not found!");
        }

        classroomRepository.deleteById(id);
    }
}