package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Assignment;
import com.smartclassroom.erp.entity.AssignmentSubmission;
import com.smartclassroom.erp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignmentOrderBySubmittedAtDesc(Assignment assignment);
    List<AssignmentSubmission> findByStudentOrderBySubmittedAtDesc(Student student);
    Optional<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, Student student);
}
