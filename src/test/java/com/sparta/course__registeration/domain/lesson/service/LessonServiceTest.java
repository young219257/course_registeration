package com.sparta.course__registeration.domain.lesson.service;

import com.sparta.course__registeration.domain.lesson.dto.AddLessonRequestDto;
import com.sparta.course__registeration.domain.lesson.dto.GetLessonsRequestDto;
import com.sparta.course__registeration.domain.lesson.dto.LessonResponseDto;
import com.sparta.course__registeration.domain.lesson.entity.Lesson;
import com.sparta.course__registeration.domain.lesson.repository.LessonRepository;
import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.student.entity.Student;
import com.sparta.course__registeration.domain.student.repository.StudentRepository;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class LessonServiceTest {

    private LessonService lessonService;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private TimeSlotRepository timeSlotRepository;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    TutorRepository tutorRepository;

    @BeforeEach
    void setUp() {
        lessonService = new LessonServiceImpl(lessonRepository, studentRepository,timeSlotRepository);
    }


    @Test
    @DisplayName("시간대, 수업길이(30분), 튜터로 수강신청 성공")
    void signUpLessonForThirtyMinutes() {
        // Given
        Long studentId = 1L;
        Student student = createStudent();
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        Long tutor1Id = 2L;
        Tutor tutor1 = createTutor(tutor1Id);

        LocalDateTime timeSlotTime = LocalDateTime.of(2024, 6, 14, 6, 30, 0);
        TimeSlot timeSlot1 = createTimeSlot(1L, tutor1, timeSlotTime, true);

        // 모킹 설정
        when(timeSlotRepository.findByStartTimeAndTutorId(any(LocalDateTime.class), any(Long.class)))
                .thenReturn(Optional.of(timeSlot1));

        AddLessonRequestDto requestDto = AddLessonRequestDto.builder()
                .studentId(studentId)
                .timeSlot(timeSlotTime)
                .tutorId(tutor1Id)
                .classPath(ClassPath.THIRTY)
                .build();

        // When
        Lesson response = lessonService.signUpLesson(requestDto);
        // Then
        // findByStartTimeAndTutorId가 올바른 인자로 호출되었는지 검증
        verify(timeSlotRepository).findByStartTimeAndTutorId(any(),any());

        assertThat(response.getTimeslot().getStartTime()).isEqualTo(timeSlotTime);
        assertThat(response.getTutor().getId()).isEqualTo(tutor1Id);
        assertThat(response.getTimeslot().getId()).isEqualTo(timeSlot1.getId());
        assertThat(response.getStudent().getId()).isEqualTo(studentId);
    }

    @Test
    @DisplayName("시간대, 수업길이(60분), 튜터로 수강신청 성공")
    void signUpLessonForSixtyMinutes() {
        // Given
        Long studentId = 1L;
        Student student = createStudent();
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        Long tutor1Id = 2L;
        Tutor tutor1 = createTutor(tutor1Id);

        LocalDateTime timeSlotTime = LocalDateTime.of(2024, 6, 14, 6, 30, 0);
        TimeSlot timeSlot1 = createTimeSlot(1L, tutor1, timeSlotTime, true);
        TimeSlot timeSlot2 = createTimeSlot(2L, tutor1, timeSlotTime.plusMinutes(30), true);

        // mock 설정
        when(timeSlotRepository.findByStartTimeAndTutorId(any(LocalDateTime.class), any(Long.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime startTime = invocation.getArgument(0);
                    Long tutorId = invocation.getArgument(1);

                    // 첫 번째 호출에 대한 응답
                    if (startTime.equals(LocalDateTime.of(2024, 6, 14, 6, 30)) && tutorId.equals(2L)) {
                        return Optional.of(timeSlot1);
                    }
                    // 두 번째 호출에 대한 응답
                    else if (startTime.equals(LocalDateTime.of(2024, 6, 14, 7, 0)) && tutorId.equals(2L)) {
                        return Optional.of(timeSlot2);
                    } else {
                        return Optional.empty();
                    }
                });


        AddLessonRequestDto requestDto = AddLessonRequestDto.builder()
                .studentId(studentId)
                .timeSlot(timeSlotTime)
                .tutorId(tutor1Id)
                .classPath(ClassPath.SIXTY)
                .build();


        // When
        Lesson response = lessonService.signUpLesson(requestDto);

        // Then
        // findByStartTimeAndTutorId가 2번 호출되어야 함
        verify(timeSlotRepository, times(2))
                .findByStartTimeAndTutorId(any(LocalDateTime.class), any(Long.class));

        assertThat(response.getTimeslot().getStartTime()).isEqualTo(timeSlotTime);
        assertThat(response.getTutor().getId()).isEqualTo(tutor1Id);
        assertThat(response.getTimeslot().getId()).isEqualTo(timeSlot1.getId());
        assertThat(response.getStudent().getId()).isEqualTo(studentId);
    }

    @Test
    @DisplayName("수업 목록 조회 - 성공")
    void getAllLessons() {
        // Given
        Student student = createStudent();
        Tutor tutor = createTutor(1L);


        TimeSlot timeSlot1 = createTimeSlot(1L, tutor, LocalDateTime.of(2024, 6, 14, 6, 30), true);
        TimeSlot timeSlot2 = createTimeSlot(2L, tutor, LocalDateTime.of(2024, 6, 14, 8, 0), true);

        // 두 개의 레슨 생성
        Lesson lesson1 = createLesson(1L, student, tutor, timeSlot1, ClassPath.THIRTY);
        Lesson lesson2 = createLesson(2L, student, tutor, timeSlot2, ClassPath.SIXTY);

        // Lesson 리스트 생성 및 추가
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson1);
        lessons.add(lesson2);

        //mock 설정
        when(lessonRepository.findAllByStudentId(student.getId())).thenReturn(lessons);
        // RequestDto 생성
        GetLessonsRequestDto requestDto=GetLessonsRequestDto.builder().studentId(student.getId()).build();

        // When
        List<LessonResponseDto> response = lessonService.getAllLessons(requestDto);
        // Then
        assertEquals(response.size(),2);
        assertEquals(lesson1.getId(), response.get(0).getLessonId());
        assertEquals(lesson2.getId(), response.get(1).getLessonId());

    }

    // Lesson 객체를 생성하는 헬퍼 메서드
    private Lesson createLesson(Long lessonId, Student student, Tutor tutor, TimeSlot timeSlot, ClassPath classPath) {
        return Lesson.builder()
                .id(lessonId)
                .student(student)
                .tutor(tutor)
                .timeslot(timeSlot)
                .classPath(classPath)
                .build();
    }

    // Student 객체를 생성하는 헬퍼 메서드
    private Student createStudent() {
        return Student.builder()
                .id(1L)
                .name("김영아")
                .build();
    }

    // Tutor 객체를 생성하는 헬퍼 메서드
    private Tutor createTutor(Long tutorId) {
        return Tutor.builder()
                .id(tutorId)
                .name("tutor"+tutorId)
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