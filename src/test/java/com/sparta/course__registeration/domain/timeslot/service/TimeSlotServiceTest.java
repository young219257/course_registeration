package com.sparta.course__registeration.domain.timeslot.service;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotResponseDto;
import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.domain.tutor.repository.TutorRepository;
import com.sparta.course__registeration.global.exception.handler.TimeSlotDeletionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TimeSlotServiceTest {

    TimeSlotService timeSlotService;

    @MockBean
    private TimeSlotRepository timeSlotRepository;

    @MockBean
    TutorRepository tutorRepository;

    @BeforeEach
    void setUp() {
        timeSlotService = new TimeSlotServiceImpl(timeSlotRepository,tutorRepository);
    }

    @Test
    @DisplayName("시간대 생성 - 성공")
    void addTimeSlot() {
        // Given
        Long tutorId = 1L;
        Tutor tutor = createTutor(tutorId);
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));

        List<LocalDateTime> availableTimeslots = Arrays.asList(
                LocalDateTime.of(2024, 7, 12, 9, 0),
                LocalDateTime.of(2024, 7, 12, 10, 0)
        );

        TimeSlotRequestDto requestDto = TimeSlotRequestDto.builder()
                .availableTimeSlots(availableTimeslots)
                .build();

        //중복된 시간대가 없다고 가정
        when(timeSlotRepository.existsByTutorAndStartTime(any(), any())).thenReturn(false);

        // saveAll이 호출되면 입력으로 받은 리스트를 그대로 반환
        when(timeSlotRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<TimeSlot> createdTimeSlots = timeSlotService.addTimeSlot(tutorId, requestDto);

        // Then
        assertEquals(2, createdTimeSlots.size()); // 두 개의 타임슬롯이 생성되어야 함
        assertEquals(tutor, createdTimeSlots.get(0).getTutor());
        assertEquals(tutor, createdTimeSlots.get(1).getTutor());
    }

    @DisplayName("시간대 삭제 - 성공")
    @Test
    void deleteTimeSlotSuccess() {
        // Given
        Long tutorId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 7, 12, 5, 0, 0);

        // Tutor 객체 생성 및 저장
        Tutor tutor = createTutor(tutorId);
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));

        // TimeSlot 객체 생성 및 저장
        TimeSlot timeSlot = createTimeSlot(1L,tutor,startTime,true);
        when(timeSlotRepository.findByStartTimeAndTutorId(timeSlot.getStartTime(), tutorId))
                .thenReturn(Optional.of(timeSlot));


        // Delete 요청 객체 생성
        DeleteTimeSlotRequestDto requestDto = DeleteTimeSlotRequestDto.builder()
                .timeSlot(timeSlot.getStartTime())
                .build();

        // When
        Long response = timeSlotService.deleteTimeSlot(tutorId, requestDto).getId();
        // Then
        assertThat(response).isEqualTo(timeSlot.getId());
          }

    @DisplayName("시간대 삭제 - 실패 : 이미 예약된 상태의 경우")
    @Test
    void deleteTimeSlotFail() {
        // Given
        Long tutorId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2024, 7, 12, 5, 0, 0);

        // Tutor 객체 생성 및 저장
        Tutor tutor = createTutor(tutorId);
        when(tutorRepository.findById(tutorId)).thenReturn(Optional.of(tutor));

        // TimeSlot 객체 생성 및 저장
        TimeSlot timeSlot = createTimeSlot(1L, tutor,startTime,false);
        when(timeSlotRepository.findByStartTimeAndTutorId(timeSlot.getStartTime(), tutorId))
                .thenReturn(Optional.of(timeSlot));


        // Delete 요청 객체 생성
        DeleteTimeSlotRequestDto requestDto = DeleteTimeSlotRequestDto.builder()
                .timeSlot(timeSlot.getStartTime())
                .build();

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> timeSlotService.deleteTimeSlot(tutorId, requestDto))
                .isInstanceOf(TimeSlotDeletionException.class)
                .hasMessage("해당 시간대는 이미 예약되어 삭제할 수 없습니다.");

    }

    @DisplayName("기간 & 수업 길이(30분)로 현재 수업 가능한 시간대를 조회")
    @Test
    void getAvailableTimeSlotsForThirtyMinuteClass() {
        // Given
        Long tutorId1 = 1L;
        Long tutorId2 = 2L;

        Tutor tutor1 = createTutor(tutorId1);
        Tutor tutor2 = createTutor(tutorId2);

        // 수업 가능한 시간대 생성
        TimeSlot slot1 = createTimeSlot(1L, tutor1, LocalDateTime.of(2023, 6, 12, 5, 0), true); // 수업 가능
        TimeSlot slot2 = createTimeSlot(2L, tutor1, LocalDateTime.of(2023, 6, 12, 5, 30), true); // 수업 가능
        TimeSlot slot3 = createTimeSlot(3L, tutor1, LocalDateTime.of(2023, 6, 12, 14, 30), true); // 수업 가능
        TimeSlot slot4 = createTimeSlot(4L, tutor2, LocalDateTime.of(2023, 6, 13, 6, 0), true); // 수업 가능
        TimeSlot slot5 = createTimeSlot(5L, tutor2, LocalDateTime.of(2023, 6, 13, 15, 0), true); // 수업 가능
        TimeSlot slot6 = createTimeSlot(6L, tutor2, LocalDateTime.of(2023, 6, 13, 10, 30), false); // 예약 불가능한 시간

        when(tutorRepository.findById(tutorId1)).thenReturn(Optional.of(tutor1));
        when(tutorRepository.findById(tutorId2)).thenReturn(Optional.of(tutor2));

        List<TimeSlot> timeSlots = Arrays.asList(slot1, slot2, slot3, slot4, slot5, slot6);


        // timeSlotRepository에서 지정된 기간 동안의 시간대 조회를 모킹 -> 여기 문제
        when(timeSlotRepository.findAllByStartTimeBetween(any(), any())).thenReturn(timeSlots);

        AvailableTimeslotRequestDto requestDto = AvailableTimeslotRequestDto.builder()
                .startDate(LocalDate.of(2023, 6, 12))
                .endDate(LocalDate.of(2023, 6, 12))
                .classPath(ClassPath.THIRTY)
                .build();

        // When
        List<AvailableTimeslotResponseDto> availableTimeSlots = timeSlotService.getAvailableTimeSlots(requestDto);

        // Then
        assertEquals(3, availableTimeSlots.size()); // 기대하는 시간대 개수

        // 예상되는 결과와 매칭
        List<String> expectedTimeSlots = List.of(
                "2023-06-12T05:00:00Z",
                "2023-06-12T05:30:00Z",
                "2023-06-12T14:30:00Z"
        );

        for (String expected : expectedTimeSlots) {
            assertTrue(availableTimeSlots.stream()
                            .anyMatch(slot -> slot.getAvailableTimeSlot().equals(expected)),
                    "Expected time slot: " + expected + " not found");
        }

    }

    // Tutor 객체를 생성하는 헬퍼 메서드
    private Tutor createTutor(Long tutorId) {
        return Tutor.builder()
                .id(tutorId)
                .name("테스트 튜터")
                .build();
    }

    // TimeSlot 객체를 생성하는 헬퍼 메서드
    private TimeSlot createTimeSlot(Long timeSlotId, Tutor tutor,LocalDateTime startTime, Boolean isAvailable) {
        return TimeSlot.builder()
                .id(timeSlotId)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .tutor(tutor)
                .isAvailable(isAvailable)
                .build();
    }
}