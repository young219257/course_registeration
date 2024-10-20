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
import com.sparta.course__registeration.global.exception.handler.ErrorCode;
import com.sparta.course__registeration.global.exception.handler.NotFoundResourceException;
import com.sparta.course__registeration.global.exception.handler.TimeSlotNotAvailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final TimeSlotRepository timeSlotRepository;


    @Override
    @Transactional
    public void signUpLesson(AddLessonRequestDto addLessonRequestDto) {

        Student student=findStudentById(addLessonRequestDto.getStudentId());
        TimeSlot timeSlot=findTimeSlotByStartTimeAndTutorId(addLessonRequestDto.getTimeSlot(), addLessonRequestDto.getTutorId());
        ClassPath classPath=addLessonRequestDto.getClassPath();

        //이미 예약이 되어있는 시간대의 경우
        if(!timeSlot.isAvailable()){
            throw new TimeSlotNotAvailableException(ErrorCode.ALREADY_BOOKING_TIMESLOT);
        }

        Lesson lesson=Lesson.of(classPath, timeSlot.getTutor(), student,timeSlot);
        lessonRepository.save(lesson);

        //예약 완료 상태로 변경
        timeSlot.updateIsAvailable();

        if(classPath.equals(ClassPath.SIXTY)){
            //timeSlot의 endTime이 startTime이며 tutor가 동일한 TimeSlot을 찾아 lesson에 추가
            TimeSlot nextTimeSlot = findTimeSlotByStartTimeAndTutorId(timeSlot.getEndTime(),timeSlot.getTutor().getId());
            Lesson nextLesson =Lesson.of(classPath, nextTimeSlot.getTutor(), student,nextTimeSlot);
            lessonRepository.save(nextLesson);

            //예약 완료 상태로 변경
            nextTimeSlot.updateIsAvailable();
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponseDto> getAllLessons(GetLessonsRequestDto getLessonsRequestDto) {

        List<Lesson> lessons=lessonRepository.findAllByStudentId(getLessonsRequestDto.getStudentId());

        return lessons.stream()
                .map(LessonResponseDto::from)
                .collect(Collectors.toList());
    }

    private Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_STUDENT));
    }

    private TimeSlot findTimeSlotByStartTimeAndTutorId(LocalDateTime startTime, Long tutorId) {
        return timeSlotRepository.findByTutorIdAndStartTimeBetween(tutorId,startTime,startTime.plusMinutes(30)).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_TIMESLOT));
    }
}
