package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository
        extends JpaRepository<Faculty, Long> {

    // Find faculty by email
    Optional<Faculty> findByEmail(String email);

    // Find faculty by department
    List<Faculty> findByDepartment(String department);

    // Find faculty by designation
    List<Faculty> findByDesignation(String designation);

    // Find faculty by subject taught
    List<Faculty> findBySubjectsTaught(String subjectsTaught);

    // Find faculty by department and designation
    List<Faculty> findByDepartmentAndDesignation(
            String department,
            String designation
    );

    // Check email exists
    boolean  existsByEmail(String email);
}