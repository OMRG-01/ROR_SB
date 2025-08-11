package com.java.eONE.controller;

import com.java.eONE.DTO.AssignmentRequestDTO;
import com.java.eONE.DTO.AssignmentResponseDTO;
import com.java.eONE.model.Assignment;
import com.java.eONE.model.Notification;
import com.java.eONE.model.Subject;
import com.java.eONE.model.User;
import com.java.eONE.repository.AssignmentRepository;
import com.java.eONE.repository.NotificationRepository;
import com.java.eONE.repository.SubjectRepository;
import com.java.eONE.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/assignments")

public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final NotificationRepository notificationRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public AssignmentController(AssignmentRepository assignmentRepository,
                                NotificationRepository notificationRepository,
                                SubjectRepository subjectRepository,
                                UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.notificationRepository = notificationRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentRequestDTO dto) {
        // Validate related entities
        Optional<Subject> subjectOpt = subjectRepository.findById(dto.getSubjectId());
        Optional<User> teacherOpt = userRepository.findById(dto.getTeacherId());

        if (subjectOpt.isEmpty() || teacherOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid subject_id or teacher_id"));
        }

        Assignment assignment = new Assignment();
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setDueDate(dto.getDueDate());
        assignment.setFile(dto.getFile());
        assignment.setSubject(subjectOpt.get());
        assignment.setTeacher(teacherOpt.get());
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        Assignment saved = assignmentRepository.save(assignment);

        Notification notification = new Notification();
        notification.setTeacher(teacherOpt.get());
        notification.setAssignment(saved);
        notification.setMessage(teacherOpt.get().getName() + " has submitted a new assignment: " + saved.getTitle());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        AssignmentResponseDTO responseDTO = mapToResponseDTO(saved);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Assignment created successfully");
        response.put("assignment", responseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAssignments(@RequestParam(required = false) Long teacher_id,
                                            @RequestParam(required = false) Long student_id) {

        if (teacher_id != null) {
            List<Assignment> assignments = assignmentRepository.findByTeacherId(teacher_id);
            List<AssignmentResponseDTO> dtos = assignments.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } else if (student_id != null) {
            Optional<User> studentOpt = userRepository.findById(student_id);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid student_id"));
            }
            var classroom = studentOpt.get().getClassroom();
            if (classroom == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            List<Long> subjectIds = classroom.getSubjects().stream()
                    .map(sub -> sub.getId())
                    .collect(Collectors.toList());
            List<Assignment> assignments = assignmentRepository.findBySubjectIdIn(subjectIds);
            List<AssignmentResponseDTO> dtos = assignments.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "teacher_id or student_id parameter required"));
    }

    @GetMapping("/{id}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable Long id) {
        var submissions = assignmentRepository.findSubmissionsByAssignmentId(id);
        // You need to implement this in your repository or service
        // Also map to DTO for submissions, omitted here for brevity
        return ResponseEntity.ok(submissions);
    }

    private AssignmentResponseDTO mapToResponseDTO(Assignment assignment) {
        AssignmentResponseDTO dto = new AssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setDueDate(assignment.getDueDate());
        dto.setFile(assignment.getFile());
        dto.setSubjectId(assignment.getSubject().getId());
        dto.setTeacherId(assignment.getTeacher() != null ? assignment.getTeacher().getId() : null);
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        dto.setFileUrl("https://yourdomain.com/uploads/" + assignment.getFile());
        return dto;
    }
}
