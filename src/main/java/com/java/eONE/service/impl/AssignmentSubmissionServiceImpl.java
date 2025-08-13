package com.java.eONE.service.impl;

import com.java.eONE.model.AssignmentSubmission;
import com.java.eONE.repository.AssignmentSubmissionRepository;
import com.java.eONE.service.AssignmentSubmissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;

    public AssignmentSubmissionServiceImpl(AssignmentSubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }
    
    @Override
    public List<AssignmentSubmission> findByUserId(Long userId) {
        return submissionRepository.findByUserId(userId);
    }


    @Override
    public AssignmentSubmission saveSubmission(AssignmentSubmission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public Optional<AssignmentSubmission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    @Override
    public AssignmentSubmission updateMarksAndGrade(Long id, Integer marks, String grade) {
        Optional<AssignmentSubmission> optionalSubmission = submissionRepository.findById(id);
        if (optionalSubmission.isPresent()) {
            AssignmentSubmission submission = optionalSubmission.get();
            submission.setMarks(marks);
            submission.setGrade(grade);
            return submissionRepository.save(submission);
        } else {
            throw new RuntimeException("AssignmentSubmission not found with id " + id);
        }
    }
}
