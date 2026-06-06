package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.entity.Stream;
import com.smartclassroom.erp.repository.SectionRepository;
import com.smartclassroom.erp.repository.StreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StreamRepository streamRepository;

    // Get all sections
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    // Get section by id
    public Optional<Section> getSectionById(Long id) {
        return sectionRepository.findById(id);
    }

    // Get sections by stream id
    public List<Section> getSectionsByStream(Long streamId) {
        Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> new RuntimeException("Stream not found!"));
        return sectionRepository.findByStream(stream);
    }

    // Get sections by stream and semester
    public List<Section> getSectionsByStreamAndSemester(
            Long streamId, Integer semester) {
        Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> new RuntimeException("Stream not found!"));
        return sectionRepository.findByStreamAndSemester(stream, semester);
    }

    // Create new section
    public Section createSection(Long streamId, Section section) {

        // Step 1: Find stream
        Stream stream = streamRepository.findById(streamId)
            .orElseThrow(() -> new RuntimeException("Stream not found!"));

        // Step 2: Business logic
        // Check if section already exists in stream and semester
        if (sectionRepository.existsByNameAndStreamAndSemester(
                section.getName(), stream, section.getSemester())) {
            throw new RuntimeException(
                "Section " + section.getName() +
                " already exists in this stream and semester!");
        }

        // Step 3: Set stream
        section.setStream(stream);

        // Step 4: Save and return
        return sectionRepository.save(section);
    }

    // Update section
    public Section updateSection(Long id, Section updatedSection) {
        Section existing = sectionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Section not found!"));

        existing.setName(updatedSection.getName());
        existing.setSemester(updatedSection.getSemester());

        return sectionRepository.save(existing);
    }

    // Delete section
    public void deleteSection(Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new RuntimeException("Section not found!");
        }
        sectionRepository.deleteById(id);
    }
}
