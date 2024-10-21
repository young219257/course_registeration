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

import java.time.LocalDateTime;
import java.util.List;

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
    @DisplayName("강의 생성")
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
        //Given
        Student student = studentRepository.save(createStudent());
        Tutor tutor1 = tutorRepository.save(createTutor(1L));
        Tutor tutor2 = tutorRepository.save(createTutor(2L));
        TimeSlot timeSlot1 = timeSlotRepository.save(createTimeSlot(1L, tutor1, LocalDateTime.of(2024, 6, 14, 6, 30), true));
        TimeSlot timeSlot2 = timeSlotRepository.save(createTimeSlot(2L, tutor2, LocalDateTime.of(2024, 6, 14, 10, 30), true));

        Lesson lesson1 = lessonRepository.save(createLesson(1L,student,tutor1,timeSlot1, ClassPath.valueOf("THIRTY")));
        Lesson lesson2 = lessonRepository.save(createLesson(2L,student,tutor2,timeSlot2, ClassPath.valueOf("SIXTY")));

        //When
        List<Lesson> response = lessonRepository.findAllByStudentId(student.getId());
        //Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(lesson1.getStudent().getId(), student.getId());
        assertEquals(lesson2.getStudent().getId(), student.getId());

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

    // 헬퍼 메서드 : Lesson 생성
    private Lesson createLesson(Long lessonId, Student student, Tutor tutor, TimeSlot timeSlot, ClassPath classPath) {
        return Lesson.builder()
                .id(lessonId)
                .student(student)
                .tutor(tutor)
                .timeslot(timeSlot)
                .classPath(classPath)
                .build();
    }
}