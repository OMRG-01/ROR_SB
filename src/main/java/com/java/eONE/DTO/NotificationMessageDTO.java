package com.java.eONE.DTO;

import java.time.LocalDateTime;

public class NotificationMessageDTO {
    private String message;
    private LocalDateTime createdAt;

    public NotificationMessageDTO(String message, LocalDateTime createdAt) {
        this.message = message;
        this.createdAt = createdAt;
    }

    // getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
