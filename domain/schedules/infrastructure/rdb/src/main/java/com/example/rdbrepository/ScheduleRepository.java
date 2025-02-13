package com.example.rdbrepository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedules, Long> {

    List<Schedules> findByCategoryId(Long categoryId);

    List<Schedules> findByUserId(Long userId);
}
