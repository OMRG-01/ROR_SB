package com.java.eONE.controller;

import com.java.eONE.DTO.TeacherDashboardCountDTO;
import com.java.eONE.DTO.UserResponseDTO;
import com.java.eONE.model.Classroom;
import com.java.eONE.model.Role;
import com.java.eONE.model.User;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.RoleRepository;
import com.java.eONE.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ClassroomRepository classroomRepository;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user,
                                      @RequestParam(required = false) Long roleId,
                                      @RequestParam(required = false) Long classroomId) {

        if (roleId != null) {
            Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }

        if (classroomId != null) {
            Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
            user.setClassroom(classroom);
        }

        UserResponseDTO userDTO = userService.registerUser(user);
        return ResponseEntity.status(201).body(Map.of(
            "message", "User registered successfully. Once the request is approved, you can log in.",
            "user", userDTO
        ));
    }


    @GetMapping("/pending_approvals")
    public ResponseEntity<?> getPendingApprovals(@RequestParam String type, @RequestParam(required = false) Long teacherId) {
        List<UserResponseDTO> users = userService.getPendingApprovals(type, teacherId);
        return ResponseEntity.ok(Map.of("users", users));
    }

    @GetMapping("/approved_users")
    public ResponseEntity<?> getApprovedUsers(@RequestParam String type, @RequestParam(required = false) Long teacherId) {
        List<UserResponseDTO> users = userService.getApprovedUsers(type, teacherId);
        return ResponseEntity.ok(Map.of("users", users));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        try {
            // Approve user in service
            userService.approveUser(id);

            // Fetch updated user details
            UserResponseDTO updatedUser = userService.getUserById(id);

            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser); // ✅ Return full user object
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Long id) {
        try {
            // Reject user in service
            userService.rejectUser(id);

            // Fetch updated user details
            UserResponseDTO updatedUser = userService.getUserById(id);

            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser); // ✅ Return full user object
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(Map.of("users", users));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserResponseDTO userDTO = userService.getUserById(id);
        if (userDTO == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Invalid user id, user not found"));
        }
        return ResponseEntity.ok(Map.of("user", userDTO));
    }

    @GetMapping("/admin_dashboard_count")
    public ResponseEntity<?> adminDashboardCount() {
        long pendingApprovals = userService.getPendingApprovalsCount();
        long classroomCount = userService.getClassroomCount();
        return ResponseEntity.ok(Map.of(
                "pending_approvals_count", pendingApprovals,
                "classroom_count", classroomCount
        ));
    }

    @GetMapping("/{teacherId}/teacher_dashboard_count")
    public ResponseEntity<?> teacherDashboardCount(@PathVariable Long teacherId) {
        TeacherDashboardCountDTO countDto = userService.getTeacherDashboardCount(teacherId);
        return ResponseEntity.ok(countDto);
    }

}
