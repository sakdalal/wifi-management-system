package com.sak.wifi.repository;


import com.sak.wifi.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findByUserTypeAndUserIdOrderByCreatedAtDesc(String userType, Long userId);

    List<Notification> findByUserTypeAndUserIdAndReadFalse(String userType, Long userId);

    long countByUserTypeAndUserIdAndReadFalse(String userType, Long userId);


}
