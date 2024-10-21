package com.sparta.course__registeration.domain.tutor.service;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
import com.sparta.course__registeration.domain.tutor.dto.TutorRequestDto;
import com.sparta.course__registeration.domain.tutor.dto.TutorResponseDto;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.domain.tutor.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TutorServiceTest {

    TutorService tutorService;

    @MockBean
    private TimeSlotRepository timeSlotRepository;

    @MockBean
    private TutorRepository tutorRepository;


    @BeforeEach
    void setUp() {
        tutorService = new TutorServiceImpl(timeSlotRepository);
    }

    @DisplayName("수업 가능한 튜터 조회 성공 - 수업길이(30분)")
    @Test
    void getAvailableTutorsForThirtyMinuteClass() {
        // Given
        Long tutorId1 = 1L;
        Long tutorId2 = 2L;

        Tutor tutor1 = createTutor(tutorId1, "John Smith");
        Tutor tutor2 = createTutor(tutorId2, "Jennie Ruby Jane");

        when(tutorRepository.findById(tutorId1)).thenReturn(Optional.of(tutor1));
        when(tutorRepository.findById(tutorId2)).thenReturn(Optional.of(tutor2));

        List<TimeSlot> timeSlots = new ArrayList<>();

        TimeSlot timeSlot1 = createTimeSlot(1L, tutor1, LocalDateTime.of(2023, 6, 14, 6, 30), true);
        TimeSlot timeSlot2 = createTimeSlot(2L, tutor2, LocalDateTime.of(2023, 6, 14, 7, 0), true);
        TimeSlot timeSlot3 = createTimeSlot(3L, tutor2, LocalDateTime.of(2023, 6, 14, 6, 30), true);
        TimeSlot timeSlot4 = createTimeSlot(4L, tutor2, LocalDateTime.of(2023, 6, 14, 8, 0), true);
        timeSlots.add(timeSlot1);
        timeSlots.add(timeSlot2);
        timeSlots.add(timeSlot3);
        timeSlots.add(timeSlot4);

        // 조회할 시간 범위 정의
        LocalDateTime startTime = LocalDateTime.of(2023, 6, 14, 6, 30);

        when(timeSlotRepository.findAllByStartTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(timeSlots);

        // TutorRequestDto 생성
        TutorRequestDto requestDto = TutorRequestDto.builder()
                .timeSlot(startTime) // 요청된 시간
                .classPath(ClassPath.THIRTY)
                .build();

        // When
        List<TutorResponseDto> availableTutors = tutorService.getAvailableTutors(requestDto);

        // Then
        assertNotNull(availableTutors);
        assertEquals(2, availableTutors.size());
        assertEquals(tutorId1, availableTutors.get(0).getTutorId());
        assertEquals("John Smith", availableTutors.get(0).getTutorName());
        assertEquals(tutorId2, availableTutors.get(1).getTutorId());
        assertEquals("Jennie Ruby Jane", availableTutors.get(1).getTutorName());
    }

    @DisplayName("수업 가능한 튜터 조회 성공 - 수업길이(60분)")
    @Test
    void getAvailableTutorsForSixtyMinuteClass() {
        // Given
        Long tutor1Id = 1L;
        Long tutor2Id = 2L;

        Tutor tutor1 = createTutor(tutor1Id, "John Smith");
        Tutor tutor2 = createTutor(tutor2Id, "Jennie Ruby Jane");

        when(tutorRepository.findById(tutor1Id)).thenReturn(Optional.of(tutor1));
        when(tutorRepository.findById(tutor2Id)).thenReturn(Optional.of(tutor2));

        List<TimeSlot> timeSlots = new ArrayList<>();

        TimeSlot timeSlot1 = createTimeSlot(1L, tutor1, LocalDateTime.of(2023, 6, 14, 6, 30), true);
        TimeSlot timeSlot2 = createTimeSlot(2L, tutor1, LocalDateTime.of(2023, 6, 14, 7, 0), true);
        TimeSlot timeSlot3 = createTimeSlot(3L, tutor2, LocalDateTime.of(2023, 6, 14, 6, 30), true);
        TimeSlot timeSlot4 = createTimeSlot(4L, tutor2, LocalDateTime.of(2023, 6, 14, 8, 0), true);
        timeSlots.add(timeSlot1);
        timeSlots.add(timeSlot2);
        timeSlots.add(timeSlot3);
        timeSlots.add(timeSlot4);


        // 조회할 시간 범위 정의
        LocalDateTime startTime = LocalDateTime.of(2023, 6, 14, 6, 30);
        LocalDateTime endTime = startTime.plusMinutes(30);

        when(timeSlotRepository.findAllByStartTimeBetween(startTime,endTime)).thenReturn(timeSlots);

        // TutorRequestDto 생성
        TutorRequestDto requestDto = TutorRequestDto.builder()
                .timeSlot(startTime) // 요청된 시간
                .classPath(ClassPath.SIXTY)
                .build();

        // When
        List<TutorResponseDto> availableTutors = tutorService.getAvailableTutors(requestDto);

        // Then
        assertNotNull(availableTutors);
        assertEquals(1, availableTutors.size());
        assertEquals(tutor1Id, availableTutors.get(0).getTutorId());
        assertEquals("John Smith", availableTutors.get(0).getTutorName());
    }

    // Tutor 객체를 생성하는 헬퍼 메서드
    private Tutor createTutor(Long tutorId, String name) {
        return Tutor.builder()
                .id(tutorId)
                .name(name)
                .build();
    }

    // TimeSlot 객체를 생성하는 헬퍼 메서드
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