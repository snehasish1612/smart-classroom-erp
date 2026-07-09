package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByOrderByCreatedAtDesc();
    List<Note> findBySubjectContainingIgnoreCaseOrderByCreatedAtDesc(String subject);
}
