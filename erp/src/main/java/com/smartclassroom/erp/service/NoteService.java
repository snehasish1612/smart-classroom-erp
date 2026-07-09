package com.smartclassroom.erp.service;

import com.smartclassroom.erp.dto.NoteGenerationRequest;
import com.smartclassroom.erp.entity.Note;
import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.repository.NoteRepository;
import com.smartclassroom.erp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Note> getNotesBySubject(String subject) {
        return noteRepository.findBySubjectContainingIgnoreCaseOrderByCreatedAtDesc(subject);
    }

    public Note getNote(Long id) {
        return noteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Note not found!"));
    }

    public Note generateNote(NoteGenerationRequest request) {
        User creator = userRepository.findById(request.getCreatedByUserId())
            .orElseThrow(() -> new RuntimeException("User not found!"));

        if (creator.getRole() == User.Role.STUDENT) {
            throw new RuntimeException("Only teachers or admins can generate notes!");
        }

        Note note = new Note();
        note.setTopic(request.getTopic());
        note.setSubject(request.getSubject());
        note.setCreatedBy(creator);
        note.setContent(generateStructuredContent(request.getSubject(), request.getTopic()));

        return noteRepository.save(note);
    }

    private String generateStructuredContent(String subject, String topic) {
        return """
            AI Generated Study Notes

            Subject: %s
            Topic: %s

            1. Overview
            %s is an important topic in %s. It should be understood through its definition, core ideas, practical uses, and common examination points.

            2. Key Concepts
            - Definition and purpose of %s.
            - Main components, terminology, and workflow.
            - Real-world classroom or laboratory applications.
            - Advantages, limitations, and best practices.

            3. Detailed Explanation
            The topic can be studied by first identifying the problem it solves, then understanding the process or model used, and finally connecting it with examples. Students should focus on the sequence of operations, important formulas or diagrams if applicable, and the relationship with previously studied chapters.

            4. Example
            Consider a smart classroom ERP scenario. %s can be applied to improve academic data handling, classroom automation, reporting, communication, or resource management depending on the subject context.

            5. Short Questions
            - What is %s?
            - Why is %s important?
            - Mention two applications of %s.
            - Write one advantage and one limitation.

            6. Summary
            %s helps students connect theory with practical implementation. Revise the definitions, flow, examples, and applications before examinations.
            """.formatted(subject, topic, topic, subject, topic, topic, topic, topic, topic, topic);
    }
}
