package com.java.eONE.controller;

import com.java.eONE.DTO.ClassroomResponseDTO;
import com.java.eONE.model.Classroom;
import com.java.eONE.service.ClassroomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public ResponseEntity<List<ClassroomResponseDTO>> getAllClassrooms() {
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        List<ClassroomResponseDTO> dtos = classrooms.stream()
                .map(c -> new ClassroomResponseDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> createClassroom(@RequestBody Classroom classroom) {
        try {
            Classroom saved = classroomService.saveClassroom(classroom);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                        new ApiResponse("Classroom created successfully",
                            new ClassroomResponseDTO(saved.getId(), saved.getName())
                        )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ApiError(e.getMessage()));
        }
    }

    // Helper response classes
    static class ApiResponse {
        private String message;
        private ClassroomResponseDTO classroom;
        public ApiResponse(String message, ClassroomResponseDTO classroom) {
            this.message = message;
            this.classroom = classroom;
        }
        // getters + setters
    }
    static class ApiError {
        private String error;
        public ApiError(String error) { this.error = error; }
        // getters + setters
    }
}
