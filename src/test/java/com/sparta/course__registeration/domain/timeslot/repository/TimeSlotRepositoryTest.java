package com.sparta.course__registeration.domain.timeslot.repository;

import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.domain.tutor.repository.TutorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TimeSlotRepositoryTest {
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @DisplayName("시간대 생성")
    void createTimeSlot() {

    }

    @DisplayName("시작일, 종료일 사이의 TimeSlot 조회")
    @Test
    void findAllByStartTimeBetween() {
    }

    @DisplayName("시작일, 튜터Id 기준으로 TimeSlot 조회")
    @Test
    void findByStartTimeAndTutorId() {
    }

    @DisplayName("시작일, 튜터Id 기준 TimeSlot 유무 반환")
    @Test
    void existsByTutorAndStartTime() {
    }

    // 헬퍼 메서드: Tutor 생성
    private Tutor createTutor(Long tutorId) {
        return Tutor.builder()
                .id(tutorId)
                .name("tutor" + tutorId)
                .build();
    }

    // 헬퍼 메서드: TimeSlot 생성
    private TimeSlot createTimeSlot(Long timeSlotId, Tutor tutor, LocalDateTime startTime, Boolean isAvailable) {
        return TimeSlot.builder()
                .id(timeSlotId)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .tutor(tutor)
                .isAvailable(isAvailable)
                .build();
    }
}