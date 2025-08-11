package com.java.eONE.controller;

import com.java.eONE.DTO.AssignmentSubmissionRequestDTO;
import com.java.eONE.DTO.AssignmentSubmissionResponseDTO;
import com.java.eONE.model.*;
import com.java.eONE.repository.*;
import com.java.eONE.service.AssignmentSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/assignment-submissions")
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService submissionService;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public AssignmentSubmissionController(AssignmentSubmissionService submissionService,
                                          AssignmentRepository assignmentRepository,
                                          UserRepository userRepository,
                                          NotificationRepository notificationRepository) {
        this.submissionService = submissionService;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @PostMapping
    public ResponseEntity<?> createSubmission(@RequestBody AssignmentSubmissionRequestDTO dto) {
        var assignment = assignmentRepository.findById(dto.getAssignmentId()).orElse(null);
        var user = userRepository.findById(dto.getUserId()).orElse(null);

        if (assignment == null || user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid assignment_id or user_id"));
        }

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setUser(user);
        submission.setFile(dto.getFile());
        submission.setCreatedAt(java.time.LocalDateTime.now());
        submission.setUpdatedAt(java.time.LocalDateTime.now());

        AssignmentSubmission savedSubmission = submissionService.saveSubmission(submission);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setAssignment(assignment);
        notification.setMessage(user.getName() + " has completed an assignment: " + assignment.getTitle() + ". Please review it.");
        notificationRepository.save(notification);

        AssignmentSubmissionResponseDTO responseDTO = new AssignmentSubmissionResponseDTO();
        responseDTO.setId(savedSubmission.getId());
        responseDTO.setAssignmentId(savedSubmission.getAssignment().getId());
        responseDTO.setUserId(savedSubmission.getUser().getId());
        responseDTO.setFile(savedSubmission.getFile());
        responseDTO.setCreatedAt(savedSubmission.getCreatedAt());
        responseDTO.setUpdatedAt(savedSubmission.getUpdatedAt());
        responseDTO.setMarks(savedSubmission.getMarks());
        responseDTO.setGrade(savedSubmission.getGrade());
        responseDTO.setFileUrl("https://yourdomain.com/uploads/" + savedSubmission.getFile());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Submission successful");
        response.put("submission", responseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateMarksAndGrade(@PathVariable Long id,
                                                 @RequestParam Integer marks,
                                                 @RequestParam String grade) {
        try {
            AssignmentSubmission updatedSubmission = submissionService.updateMarksAndGrade(id, marks, grade);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Marks updated successfully");
            response.put("submission", updatedSubmission); // Consider DTO

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
