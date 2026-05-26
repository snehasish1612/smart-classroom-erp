package com.smartclassroom.erp.service;

import com.smartclassroom.erp.config.ResourceNotFoundException;
import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByRollNumber(String rollNumber) {
        return studentRepository.findByRollNumber(rollNumber);
    }

    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }

    public List<Student> getStudentsByDepartmentAndSemester(
            String department, Integer semester) {
        return studentRepository.findByDepartmentAndSemester(department, semester);
    }

    public Student saveStudent(Student student) {
        // Check duplicate email
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        // Check duplicate roll number
        if (studentRepository.existsByRollNumber(student.getRollNumber())) {
            throw new RuntimeException("Roll number already exists!");
        }
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updatedStudent) {

    Student existingStudent = studentRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Student not found!"));

    // Check email duplicate
    Optional<Student> emailStudent =
            studentRepository.findByEmail(updatedStudent.getEmail());

    if (emailStudent.isPresent()
            && !emailStudent.get().getId().equals(id)) {

        throw new RuntimeException("Email already exists!");
    }

    // Check roll number duplicate
    Optional<Student> rollStudent =
            studentRepository.findByRollNumber(updatedStudent.getRollNumber());

    if (rollStudent.isPresent()
            && !rollStudent.get().getId().equals(id)) {

        throw new RuntimeException("Roll number already exists!");
    }

    existingStudent.setName(updatedStudent.getName());
    existingStudent.setEmail(updatedStudent.getEmail());
    existingStudent.setRollNumber(updatedStudent.getRollNumber());
    existingStudent.setDepartment(updatedStudent.getDepartment());
    existingStudent.setSemester(updatedStudent.getSemester());
    existingStudent.setPhone(updatedStudent.getPhone());

    return studentRepository.save(existingStudent);
}

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found!");
        }
        studentRepository.deleteById(id);
    }
}