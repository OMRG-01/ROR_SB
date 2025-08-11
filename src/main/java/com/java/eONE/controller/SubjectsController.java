package com.java.eONE.controller;

import com.java.eONE.DTO.SubjectDTO;
import com.java.eONE.model.Subject;
import com.java.eONE.model.User;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/subjects")
public class SubjectsController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody Subject subject) {
        SubjectDTO savedDto = subjectService.createSubject(subject);
        return ResponseEntity.status(201).body(
                Map.of(
                        "message", "Subject created successfully",
                        "subject", savedDto
                )
        );
    }

    @GetMapping
    public ResponseEntity<?> getSubjectsByTeacher(@RequestParam Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        List<SubjectDTO> subjectsDto = subjectService.getSubjectsByTeacherId(user.getId());
        return ResponseEntity.ok(subjectsDto);
    }
}
