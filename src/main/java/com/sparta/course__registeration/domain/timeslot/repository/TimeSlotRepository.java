package com.sparta.course__registeration.domain.timeslot.repository;

import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findAllBystartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}
