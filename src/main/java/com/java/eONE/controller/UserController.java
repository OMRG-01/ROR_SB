package com.java.eONE.controller;

import com.java.eONE.DTO.UserResponseDTO;
import com.java.eONE.model.User;
import com.java.eONE.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        UserResponseDTO userDTO = userService.registerUser(user);
        return ResponseEntity.status(201).body(Map.of(
                "message", "User registered successfully. Once the request is approved, you can log in.",
                "user", userDTO
        ));
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<?> getPendingApprovals(@RequestParam String type, @RequestParam(required = false) Long teacherId) {
        List<UserResponseDTO> users = userService.getPendingApprovals(type, teacherId);
        return ResponseEntity.ok(Map.of("users", users));
    }

    @GetMapping("/approved-users")
    public ResponseEntity<?> getApprovedUsers(@RequestParam String type, @RequestParam(required = false) Long teacherId) {
        List<UserResponseDTO> users = userService.getApprovedUsers(type, teacherId);
        return ResponseEntity.ok(Map.of("users", users));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        boolean success = userService.approveUser(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User approved and email sent"));
        } else {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", "Failed to approve user"));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Long id) {
        boolean success = userService.rejectUser(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User rejected and email sent"));
        } else {
            return ResponseEntity.unprocessableEntity().body(Map.of("error", "Failed to reject user"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(Map.of("users", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserResponseDTO userDTO = userService.getUserById(id);
        if (userDTO == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Invalid user id, user not found"));
        }
        return ResponseEntity.ok(Map.of("user", userDTO));
    }

    @GetMapping("/admin-dashboard-count")
    public ResponseEntity<?> adminDashboardCount() {
        long pendingApprovals = userService.getPendingApprovalsCount();
        long classroomCount = userService.getClassroomCount();
        return ResponseEntity.ok(Map.of(
                "pending_approvals_count", pendingApprovals,
                "classroom_count", classroomCount
        ));
    }

    @GetMapping("/teacher-dashboard-count")
    public ResponseEntity<?> teacherDashboardCount(@RequestParam Long id) {
        // Implement similar as in ROR for teacher dashboard count, can add later if needed
        // Return dummy for now or implement service method similarly
        return ResponseEntity.ok(Map.of("message", "Teacher dashboard count - implement service method"));
    }
}
