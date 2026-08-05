package com.sak.wifi.service;

import com.sak.wifi.dto.NotificationResponseDTO;
import com.sak.wifi.entity.Notification;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public void createNotification(String title,String message,String userTye, Long userId){
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUserType(userTye);
        notification.setUserId(userId);

        notificationRepository.save(notification);
    }


    public List<NotificationResponseDTO> getNotification(String userType, Long userId){
        List<Notification> notifications= notificationRepository
                .findByUserTypeAndUserIdOrderByCreatedAtDesc(userType, userId);

        return notifications.stream()
                .map(notification->modelMapper.map(notification, NotificationResponseDTO.class))
                .toList();
    }

    public void markAsRead(Long id){
        Notification notification=notificationRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("No such notifiation"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void MarkAllAsRead(String userType, Long userId){
        List<Notification> notifications=notificationRepository.
                findByUserTypeAndUserIdAndReadFalse(userType,userId);

        notifications.forEach(notification ->
                notification.setRead(true));

        notificationRepository.saveAll(notifications);
    }

    public long getUnreadCount(String userType, Long userId){
        return  notificationRepository.countByUserTypeAndUserIdAndReadFalse(userType, userId);
    }

}
