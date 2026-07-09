package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Assignment;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByFacultyOrderByDueDateAsc(Faculty faculty);
    List<Assignment> findBySectionOrderByDueDateAsc(Section section);
    List<Assignment> findAllByOrderByDueDateAsc();
}
