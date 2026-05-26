package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByRollNumber(String rollNumber);
    List<Student> findByDepartment(String department);
    List<Student> findBySemester(Integer semester);
    List<Student> findByDepartmentAndSemester(String department, Integer semester);
    Boolean existsByEmail(String email);
    Boolean existsByRollNumber(String rollNumber);
}