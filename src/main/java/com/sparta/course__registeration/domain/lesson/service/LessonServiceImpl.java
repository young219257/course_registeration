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
    public Lesson signUpLesson(AddLessonRequestDto addLessonRequestDto) {

        Student student=findStudentById(addLessonRequestDto.getStudentId());
        ClassPath classPath=addLessonRequestDto.getClassPath();

        //시간대, 튜터에 해당하는 timeSlot 반환
        TimeSlot timeSlot=findTimeSlotByStartTimeAndTutorId(addLessonRequestDto.getTimeSlot(), addLessonRequestDto.getTutorId());

        //timeSlot의 예약 여부 확인
        if(!timeSlot.isAvailable()){
            throw new TimeSlotNotAvailableException(ErrorCode.ALREADY_BOOKING_TIMESLOT);
        }

        Lesson lesson=Lesson.of(classPath,timeSlot.getTutor(), student,timeSlot);
        lessonRepository.save(lesson);

        //예약 상태 변경
        timeSlot.updateIsAvailable();

        //수업 길이 : 60분의 경우
        if(classPath.equals(ClassPath.SIXTY)){
            //다음 TimeSlot의 예약 상태 변경
            TimeSlot nextTimeSlot = findNextTimeSlot(timeSlot);
            nextTimeSlot.updateIsAvailable();
        }

        return lesson;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponseDto> getAllLessons(GetLessonsRequestDto getLessonsRequestDto) {

        List<Lesson> lessons=findLessonByStudentId(getLessonsRequestDto.getStudentId());

        //신청 수업 유무 확인
        if(lessons.isEmpty()){
            throw new NotFoundResourceException(ErrorCode.NOTFOUND_LESSONS);
        }

        return lessons.stream()
                .map(LessonResponseDto::from)
                .collect(Collectors.toList());
    }

    private TimeSlot findNextTimeSlot(TimeSlot timeSlot){
        TimeSlot nextTimeSlot = findTimeSlotByStartTimeAndTutorId(timeSlot.getEndTime(),timeSlot.getTutor().getId());

        if(nextTimeSlot==null){
                throw new TimeSlotNotAvailableException(ErrorCode.NOTFOUND_TIMESLOT);
        }

        return timeSlot;
    }

    private List<Lesson> findLessonByStudentId(Long studentId) {
        return lessonRepository.findAllByStudentId(studentId);
    }

    private Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_STUDENT));
    }

    private TimeSlot findTimeSlotByStartTimeAndTutorId(LocalDateTime startTime, Long tutorId) {
        return timeSlotRepository.findByStartTimeAndTutorId(startTime, tutorId);
    }
}
