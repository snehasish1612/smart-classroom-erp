package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.repository.SectionRepository;
import com.smartclassroom.erp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by id
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // Get student by roll number
    public Optional<Student> getStudentByRollNumber(String rollNumber) {
        return studentRepository.findByRollNumber(rollNumber);
    }

    // Get students by section
    public List<Student> getStudentsBySection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found!"));
        return studentRepository.findBySection(section);
    }

    // Save new student
    public Student saveStudent(Long sectionId, Student student) {

        // Step 1: Find section
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found!"));
        applySectionDefaults(student, section);

        // Step 2: Business logic checks
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        if (studentRepository.existsByRollNumber(student.getRollNumber())) {
            throw new RuntimeException("Roll number already exists!");
        }

        // Step 3: Link student to section
        student.setSection(section);

        // Step 4: Save and return
        return studentRepository.save(student);
    }

    // Update student
    public Student updateStudent(Long id, Student updatedStudent) {
        Student existing = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found!"));

        existing.setName(updatedStudent.getName());
        existing.setEmail(updatedStudent.getEmail());
        existing.setRollNumber(updatedStudent.getRollNumber());
        existing.setDepartment(updatedStudent.getDepartment());
        existing.setSemester(updatedStudent.getSemester());
        existing.setPhone(updatedStudent.getPhone());
        applySectionDefaults(existing, existing.getSection());

        return studentRepository.save(existing);
    }

    // Delete student
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found!");
        }
        studentRepository.deleteById(id);
    }

    private void applySectionDefaults(Student student, Section section) {
        if (student.getDepartment() == null || student.getDepartment().isBlank()) {
            String streamName = section.getStream() == null ? null : section.getStream().getName();
            student.setDepartment(streamName == null || streamName.isBlank() ? "CSE" : streamName);
        }

        if (student.getSemester() == null) {
            student.setSemester(section.getSemester());
        }
    }
}
