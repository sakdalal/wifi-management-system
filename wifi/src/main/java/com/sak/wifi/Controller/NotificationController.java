package com.sak.wifi.Controller;

import com.sak.wifi.dto.NotificationResponseDTO;
import com.sak.wifi.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @RequestParam String userType,
            @RequestParam Long userId
    ){
            return ResponseEntity.ok(notificationService.getNotification(userType, userId));
    }

    @PutMapping("/read/{id}")
    public String markAsRead(
            @PathVariable Long id
    ){
        notificationService.markAsRead(id);
        return "Notification mark as read";
    }

    @PutMapping("/read-all")
    public  String markAllAsRead(
            @RequestParam String userType,
            @RequestParam Long userId
    ){
        notificationService.MarkAllAsRead(userType,userId);
        return  "All notification mark as read";
    }

    @GetMapping("/unread-count")
    public Long getUnreadCount(
            @RequestParam String userType,
            @RequestParam Long userId
    ){
        return notificationService.getUnreadCount(userType,userId);
    }
}
