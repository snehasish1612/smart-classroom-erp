package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.dto.AssignmentRequest;
import com.smartclassroom.erp.dto.AssignmentReviewRequest;
import com.smartclassroom.erp.dto.AssignmentSubmissionRequest;
import com.smartclassroom.erp.entity.Assignment;
import com.smartclassroom.erp.entity.AssignmentSubmission;
import com.smartclassroom.erp.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByFaculty(@PathVariable Long facultyId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByFaculty(facultyId));
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<Assignment>> getAssignmentsBySection(@PathVariable Long sectionId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsBySection(sectionId));
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.createAssignment(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Assignment> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, request));
    }

    @PostMapping("/{assignmentId}/submissions")
    public ResponseEntity<AssignmentSubmission> submitAssignment(
            @PathVariable Long assignmentId,
            @Valid @RequestBody AssignmentSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(assignmentService.submitAssignment(assignmentId, request));
    }

    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<List<AssignmentSubmission>> getSubmissionsByAssignment(
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsByAssignment(assignmentId));
    }

    @GetMapping("/submissions/student/{studentId}")
    public ResponseEntity<List<AssignmentSubmission>> getSubmissionsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsByStudent(studentId));
    }

    @PutMapping("/submissions/{submissionId}/review")
    public ResponseEntity<AssignmentSubmission> reviewSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody AssignmentReviewRequest request) {
        return ResponseEntity.ok(assignmentService.reviewSubmission(submissionId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok("Assignment deleted successfully!");
    }
}
