package com.java.eONE.controller;

import com.java.eONE.DTO.NotificationMessageDTO;
import com.java.eONE.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/notifications")
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserNotifications(
            @PathVariable Long id,
            @RequestParam(required = false) Integer limit) {

        List<NotificationMessageDTO> notifications = notificationService.getUserNotifications(id, limit);

        if (notifications == null) {
            return ResponseEntity.status(404).body("{\"error\":\"User not found\"}");
        }
        return ResponseEntity.ok(notifications);
    }
}
