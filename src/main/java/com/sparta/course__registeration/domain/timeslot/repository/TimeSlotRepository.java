package com.sparta.course__registeration.domain.timeslot.repository;

import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
}
