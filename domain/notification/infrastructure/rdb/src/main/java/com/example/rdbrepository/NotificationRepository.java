package com.example.rdbrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query(value = "select n from Notification n where n.isRead = false")
    List<Notification>findAll();
}
