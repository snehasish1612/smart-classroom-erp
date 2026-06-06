package com.smartclassroom.erp.repository;

import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // Get all sections by stream
    List<Section> findByStream(Stream stream);

    // Get all sections by semester
    List<Section> findBySemester(Integer semester);

    // Get all sections by stream and semester
    List<Section> findByStreamAndSemester(Stream stream, Integer semester);

    // Check if section exists in stream and semester
    Boolean existsByNameAndStreamAndSemester(
        String name, Stream stream, Integer semester);
}