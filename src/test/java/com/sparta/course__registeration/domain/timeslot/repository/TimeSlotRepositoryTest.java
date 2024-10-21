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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TimeSlotRepositoryTest {
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @DisplayName("시간대 생성")
    @Test
    void createTimeSlot() {
        //Given
        Tutor tutor = tutorRepository.save(createTutor(1L));
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2020, 1, 1, 1, 0);

        TimeSlot timeSlot = TimeSlot.builder()
                .id(1L)
                .startTime(startDateTime)
                .endTime(endDateTime)
                .isAvailable(true)
                .tutor(tutor).build();
        //When
        TimeSlot savedTimeSlot= timeSlotRepository.save(timeSlot);
        //Then
        assertNotNull(savedTimeSlot);
        assertNotNull(savedTimeSlot.getId());
        assertEquals(savedTimeSlot.getStartTime(), startDateTime);
        assertEquals(savedTimeSlot.getTutor().getId(), tutor.getId());


    }

    @DisplayName("시작일, 종료일 사이의 TimeSlot 조회")
    @Test
    void findAllByStartTimeBetween() {
        //Given
        Tutor tutor = tutorRepository.save(createTutor(1L));

        TimeSlot timeSlot1 = timeSlotRepository.save(createTimeSlot(1L, tutor, LocalDateTime.of(2024, 6, 14, 6, 30), true));
        TimeSlot timeSlot2 = timeSlotRepository.save(createTimeSlot(2L, tutor, LocalDateTime.of(2024, 6, 14, 7, 0), true));
        TimeSlot timeSlot3 = timeSlotRepository.save(createTimeSlot(3L, tutor, LocalDateTime.of(2024, 6, 15, 7, 30), true));
        TimeSlot timeSlot4 = timeSlotRepository.save(createTimeSlot(3L, tutor, LocalDateTime.of(2024, 6, 15, 8, 0), false));
        TimeSlot timeSlot5 = timeSlotRepository.save(createTimeSlot(4L, tutor, LocalDateTime.of(2024, 6, 16, 6, 30), false));

        // When
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 6, 16, 0, 0);

        List<TimeSlot> response = timeSlotRepository.findAllByStartTimeBetween(startDate, endDate);

        //Then
        assertNotNull(response);
        assertEquals(3, response.size());
        assertTrue(response.contains(timeSlot1));
        assertTrue(response.contains(timeSlot2));
        assertTrue(response.contains(timeSlot3));
    }

    @DisplayName("시작일, 튜터Id 기준으로 TimeSlot 조회")
    @Test
    void findByStartTimeAndTutorId() {

        //Given
        Tutor tutor = tutorRepository.save(createTutor(1L));

        TimeSlot timeSlot1 = timeSlotRepository.save(createTimeSlot(1L, tutor, LocalDateTime.of(2024, 6, 14, 6, 30), true));
        TimeSlot timeSlot2 = timeSlotRepository.save(createTimeSlot(2L, tutor, LocalDateTime.of(2024, 6, 14, 7, 0), true));
        TimeSlot timeSlot3 = timeSlotRepository.save(createTimeSlot(3L, tutor, LocalDateTime.of(2024, 6, 15, 7, 30), true));
        TimeSlot timeSlot4 = timeSlotRepository.save(createTimeSlot(3L, tutor, LocalDateTime.of(2024, 6, 15, 8, 0), false));
        TimeSlot timeSlot5 = timeSlotRepository.save(createTimeSlot(4L, tutor, LocalDateTime.of(2024, 6, 16, 6, 30), false));

        //When
        LocalDateTime startDateTime = LocalDateTime.of(2024, 6, 14, 7, 0);

        TimeSlot response = timeSlotRepository.findByStartTimeAndTutorId(startDateTime, tutor.getId());

        //Then
        assertNotNull(response);
        assertEquals(response.getStartTime(), startDateTime);
        assertEquals(response.getTutor().getId(), tutor.getId());
        assertEquals(response.getTutor().getName(), tutor.getName());
    }

    @DisplayName("시작일, 튜터Id 기준 TimeSlot 유무 반환")
    @Test
    void existsByTutorAndStartTime() {

        //Given
        Tutor tutor = tutorRepository.save(createTutor(1L));

        TimeSlot timeSlot1 = timeSlotRepository.save(createTimeSlot(1L, tutor, LocalDateTime.of(2024, 6, 14, 6, 30), true));
        TimeSlot timeSlot2 = timeSlotRepository.save(createTimeSlot(2L, tutor, LocalDateTime.of(2024, 6, 14, 7, 0), true));
        TimeSlot timeSlot3 = timeSlotRepository.save(createTimeSlot(3L, tutor, LocalDateTime.of(2024, 6, 15, 7, 30), true));
        TimeSlot timeSlot4 = timeSlotRepository.save(createTimeSlot(3L, tutor, LocalDateTime.of(2024, 6, 15, 8, 0), false));
        TimeSlot timeSlot5 = timeSlotRepository.save(createTimeSlot(4L, tutor, LocalDateTime.of(2024, 6, 16, 6, 30), false));

        //When
        LocalDateTime startDateTime = LocalDateTime.of(2024, 6, 14, 7, 0);

        boolean response = timeSlotRepository.existsByTutorAndStartTime(tutor, startDateTime);

        assertTrue(response);
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