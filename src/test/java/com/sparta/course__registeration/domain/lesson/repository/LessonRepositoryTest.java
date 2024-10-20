package com.sparta.course__registeration.domain.lesson.repository;

import com.sparta.course__registeration.domain.lesson.entity.Lesson;
import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.student.entity.Student;
import com.sparta.course__registeration.domain.student.repository.StudentRepository;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.domain.tutor.repository.TutorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LessonRepositoryTest {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Test
    @DisplayName("강의 만들기")
    void createLesson() {
        // Given
        Student student = studentRepository.save(createStudent());
        Tutor tutor = tutorRepository.save(createTutor(1L));
        TimeSlot timeSlot = timeSlotRepository.save(createTimeSlot(1L, tutor, LocalDateTime.of(2024, 6, 14, 6, 30), true));

        Lesson lesson = Lesson.builder()
                .student(student)
                .tutor(tutor)
                .timeslot(timeSlot)
                .classPath(ClassPath.THIRTY)
                .build();

        // When
        Lesson savedLesson = lessonRepository.save(lesson);

        // Then
        assertNotNull(savedLesson);
        assertNotNull(savedLesson.getId());
        assertEquals(savedLesson.getStudent().getId(), student.getId());
        assertEquals(savedLesson.getTutor().getId(), tutor.getId());

    }
    @DisplayName("학생이 신청한 강의 조회 : studentId를 통한 조회")
    @Test
    void findAllByStudentId() {

    }

    // 헬퍼 메서드: Student 생성
    private Student createStudent() {
        return Student.builder()
                .id(1L)
                .name("김영아")
                .build();
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