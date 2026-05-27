package com.smartclassroom.erp.service;

import com.smartclassroom.erp.config.ResourceNotFoundException;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.repository.FacultyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    // Get all faculty
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    // Get faculty by ID
    public Optional<Faculty> getFacultyById(Long id) {
        return facultyRepository.findById(id);
    }
    // Get faculty by email
    public Optional<Faculty> getFacultyByEmail(String email) {
        return facultyRepository.findByEmail(email);
    }

    // Get faculty by department
    public List<Faculty> getFacultyByDepartment(String department) {
        return facultyRepository.findByDepartment(department);
    }

    // Get faculty by subject taught
    public List<Faculty> getFacultyBySubject(String subjectsTaught) {
        return facultyRepository.findBySubjectsTaught(subjectsTaught);
    }

    // Save new faculty
    public Faculty saveFaculty(Faculty faculty) {

        if (facultyRepository.existsByEmail(faculty.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        return facultyRepository.save(faculty);
    }

    // Update existing faculty
    public Faculty updateFaculty(Long id, Faculty updatedFaculty) {

        Faculty existingFaculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found!"));

        existingFaculty.setName(updatedFaculty.getName());
        existingFaculty.setEmail(updatedFaculty.getEmail());
        existingFaculty.setDepartment(updatedFaculty.getDepartment());
        existingFaculty.setPhone(updatedFaculty.getPhone());
        existingFaculty.setDesignation(updatedFaculty.getDesignation());
        existingFaculty.setSubjectsTaught(
                updatedFaculty.getSubjectsTaught());

        return facultyRepository.save(existingFaculty);
    }

    // Delete faculty
    public void deleteFaculty(Long id) {

        if (!facultyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Faculty not found!");
        }

        facultyRepository.deleteById(id);
    }
}