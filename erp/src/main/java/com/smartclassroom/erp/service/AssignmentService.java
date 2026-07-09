package com.smartclassroom.erp.service;

import com.smartclassroom.erp.dto.AssignmentRequest;
import com.smartclassroom.erp.dto.AssignmentReviewRequest;
import com.smartclassroom.erp.dto.AssignmentSubmissionRequest;
import com.smartclassroom.erp.entity.Assignment;
import com.smartclassroom.erp.entity.AssignmentSubmission;
import com.smartclassroom.erp.entity.Faculty;
import com.smartclassroom.erp.entity.Section;
import com.smartclassroom.erp.entity.Student;
import com.smartclassroom.erp.repository.AssignmentRepository;
import com.smartclassroom.erp.repository.AssignmentSubmissionRepository;
import com.smartclassroom.erp.repository.FacultyRepository;
import com.smartclassroom.erp.repository.SectionRepository;
import com.smartclassroom.erp.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final FacultyRepository facultyRepository;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            AssignmentSubmissionRepository submissionRepository,
            FacultyRepository facultyRepository,
            SectionRepository sectionRepository,
            StudentRepository studentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.facultyRepository = facultyRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAllByOrderByDueDateAsc();
    }

    public List<Assignment> getAssignmentsByFaculty(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
            .orElseThrow(() -> new RuntimeException("Faculty not found!"));
        return assignmentRepository.findByFacultyOrderByDueDateAsc(faculty);
    }

    public List<Assignment> getAssignmentsBySection(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Section not found!"));
        return assignmentRepository.findBySectionOrderByDueDateAsc(section);
    }

    public Assignment createAssignment(AssignmentRequest request) {
        Faculty faculty = facultyRepository.findById(request.getFacultyId())
            .orElseThrow(() -> new RuntimeException("Faculty not found!"));

        Assignment assignment = new Assignment();
        assignment.setTitle(request.getTitle());
        assignment.setSubject(request.getSubject());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setFaculty(faculty);

        if (request.getSectionId() != null) {
            Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found!"));
            assignment.setSection(section);
        }

        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(Long id, AssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Assignment not found!"));
        Faculty faculty = facultyRepository.findById(request.getFacultyId())
            .orElseThrow(() -> new RuntimeException("Faculty not found!"));

        assignment.setTitle(request.getTitle());
        assignment.setSubject(request.getSubject());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setFaculty(faculty);

        if (request.getSectionId() == null) {
            assignment.setSection(null);
        } else {
            Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found!"));
            assignment.setSection(section);
        }

        return assignmentRepository.save(assignment);
    }

    public AssignmentSubmission submitAssignment(Long assignmentId, AssignmentSubmissionRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assignment not found!"));
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new RuntimeException("Student not found!"));

        AssignmentSubmission submission = submissionRepository
            .findByAssignmentAndStudent(assignment, student)
            .orElseGet(AssignmentSubmission::new);
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(request.getContent());
        submission.setAttachmentUrl(request.getAttachmentUrl());

        return submissionRepository.save(submission);
    }

    public List<AssignmentSubmission> getSubmissionsByAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assignment not found!"));
        return submissionRepository.findByAssignmentOrderBySubmittedAtDesc(assignment);
    }

    public List<AssignmentSubmission> getSubmissionsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found!"));
        return submissionRepository.findByStudentOrderBySubmittedAtDesc(student);
    }

    public AssignmentSubmission reviewSubmission(Long submissionId, AssignmentReviewRequest request) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found!"));

        submission.setMarks(request.getMarks());
        submission.setFeedback(request.getFeedback());
        submission.setReviewedAt(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new RuntimeException("Assignment not found!");
        }
        assignmentRepository.deleteById(id);
    }
}
