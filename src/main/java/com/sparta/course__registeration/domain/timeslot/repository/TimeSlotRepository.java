package com.sparta.course__registeration.domain.timeslot.repository;

import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findAllByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    Optional<TimeSlot> findByStartTimeAndTutorId(LocalDateTime startTime, Long tutorId);

    Optional<TimeSlot> findByTutorIdAndStartTime(Long tutorId,LocalDateTime startTime);

    boolean existsByTutorAndStartTime(Tutor tutor, LocalDateTime availableTimeSlot);
}
