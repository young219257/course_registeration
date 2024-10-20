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

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .thenReturn(timeSlot);


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
                .thenReturn(timeSlot);


        // Delete 요청 객체 생성
        DeleteTimeSlotRequestDto requestDto = DeleteTimeSlotRequestDto.builder()
                .timeSlot(timeSlot.getStartTime())
                .build();

        // When & Then: 예외 발생 검증
        assertThatThrownBy(() -> timeSlotService.deleteTimeSlot(tutorId, requestDto))
                .isInstanceOf(TimeSlotDeletionException.class)
                .hasMessage("해당 시간대는 이미 예약되어 삭제할 수 없습니다.");

    }

    @Test
    @DisplayName("30분 수업 가능한 시간대 조회")
    void getAvailableTimeSlotsForThirtyMinuteClass() {
        // Given
        List<TimeSlot> timeSlots = createMockTimeSlots();

        AvailableTimeslotRequestDto requestDto = AvailableTimeslotRequestDto.builder()
                .startDate(LocalDate.of(2023, 6, 12))
                .endDate(LocalDate.of(2023, 6, 12))
                .classPath(ClassPath.THIRTY)
                .build();

        // 날짜와 예약 가능 여부에 따라 필터링된 시간대만 반환
        when(timeSlotRepository.findAllByStartTimeBetween(
                LocalDateTime.of(2023, 6, 12, 0, 0),
                LocalDateTime.of(2023, 6, 13, 0, 0, 0)))
                .thenReturn(timeSlots.stream()
                        .filter(slot -> slot.isAvailable()) // 예약 가능한 시간대만 포함
                        .filter(slot -> slot.getStartTime().toLocalDate().isEqual(LocalDate.of(2023, 6, 12))) // 해당 날짜만 포함
                        .collect(Collectors.toList()));

        // When
        List<AvailableTimeslotResponseDto> result = timeSlotService.getAvailableTimeSlots(requestDto);

        // Then
        assertEquals(3, result.size()); // 기대하는 시간대 개수

        List<String> expectedTimeSlots = List.of(
                "2023-06-12T05:00:00Z",
                "2023-06-12T05:30:00Z",
                "2023-06-12T14:30:00Z"
        );

        for (String expected : expectedTimeSlots) {
            assertTrue(result.stream()
                            .anyMatch(slot -> slot.getAvailableTimeSlot().equals(expected)),
                    "Expected time slot: " + expected + " not found");
        }
    }


    @Test
    @DisplayName("60분 수업 가능한 시간대 조회")
    void getAvailableTimeSlotsForSixtyMinuteClass() {
        // Given
        List<TimeSlot> timeSlots = createMockTimeSlots();
        AvailableTimeslotRequestDto requestDto = AvailableTimeslotRequestDto.builder()
                .startDate(LocalDate.of(2023, 6, 12))
                .endDate(LocalDate.of(2023, 6, 12))
                .classPath(ClassPath.SIXTY)
                .build();

        when(timeSlotRepository.findAllByStartTimeBetween(
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(timeSlots);

        // When
        List<AvailableTimeslotResponseDto> result = timeSlotService.getAvailableTimeSlots(requestDto);

        // Then
        assertEquals(1, result.size()); // 60분 수업에 맞는 시간대는 하나여야 함
        assertEquals("2023-06-12T05:00:00Z", result.get(0).getAvailableTimeSlot());
    }

    // Mock 데이터 생성
    private List<TimeSlot> createMockTimeSlots() {
        Tutor tutor1 = createTutor(1L);
        Tutor tutor2 = createTutor(2L);

        return List.of(
                createTimeSlot(1L, tutor1, LocalDateTime.of(2023, 6, 12, 5, 0), true),
                createTimeSlot(2L, tutor1, LocalDateTime.of(2023, 6, 12, 5, 30), true),
                createTimeSlot(3L, tutor1, LocalDateTime.of(2023, 6, 12, 14, 30), true),
                createTimeSlot(4L, tutor2, LocalDateTime.of(2023, 6, 13, 6, 0), true),  // 다른 날짜의 시간대
                createTimeSlot(5L, tutor2, LocalDateTime.of(2023, 6, 13, 10, 30), false) // 예약 불가능
        );
    }

    // Tutor 객체 생성 헬퍼 메서드
    private Tutor createTutor(Long id) {
        return Tutor.builder()
                .id(id)
                .name("Tutor " + id)
                .build();
    }

    // TimeSlot 객체 생성 헬퍼 메서드
    private TimeSlot createTimeSlot(Long id, Tutor tutor, LocalDateTime startTime, boolean isAvailable) {
        return TimeSlot.builder()
                .id(id)
                .tutor(tutor)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .isAvailable(isAvailable)
                .build();
    }
}
