package com.java.eONE.service;

import com.java.eONE.model.AssignmentSubmission;

import java.util.Optional;

public interface AssignmentSubmissionService {
    AssignmentSubmission saveSubmission(AssignmentSubmission submission);
    Optional<AssignmentSubmission> getSubmissionById(Long id);
    AssignmentSubmission updateMarksAndGrade(Long id, Integer marks, String grade);
}
