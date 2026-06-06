package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find student by email
    Optional<Student> findByEmail(String email);

    // Find student by roll number
    Optional<Student> findByRollNumber(String rollNumber);

    // Find all students by section
    List<Student> findBySection(Section section);

    // Check if email exists
    Boolean existsByEmail(String email);

    // Check if roll number exists
    Boolean existsByRollNumber(String rollNumber);
}