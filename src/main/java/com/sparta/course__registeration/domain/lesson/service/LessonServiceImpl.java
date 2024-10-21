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


    //수강신청 메소드
    @Override
    @Transactional
    public Lesson signUpLesson(AddLessonRequestDto addLessonRequestDto) {

        Student student=findStudentById(addLessonRequestDto.getStudentId());
        ClassPath classPath=addLessonRequestDto.getClassPath();

        //시간대, 튜터에 해당하는 timeSlot 반환
        TimeSlot timeSlot= getAvailableTimeSlot(addLessonRequestDto.getTimeSlot(), addLessonRequestDto.getTutorId());

        Lesson lesson=Lesson.of(classPath,timeSlot.getTutor(), student,timeSlot);
        lessonRepository.save(lesson);

        //예약 상태 변경
        updateTimeSlotNotAvailable(timeSlot,classPath);

        return lesson;
    }

    //신청 수업 조회 메소드
    @Override
    @Transactional(readOnly = true)
    public List<LessonResponseDto> getAllLessons(GetLessonsRequestDto getLessonsRequestDto) {

        List<Lesson> lessons=findLessonByStudentId(getLessonsRequestDto.getStudentId());

        return lessons.stream()
                .map(LessonResponseDto::from)
                .collect(Collectors.toList());
    }

    //예약 상태 변경 메소드
    private void updateTimeSlotNotAvailable(TimeSlot timeSlot,ClassPath classPath){

        timeSlot.updateIsAvailable();

        //수업 길이 : 60분의 경우
        if(classPath.equals(ClassPath.SIXTY)){
            //다음 TimeSlot의 예약 상태 변경
            TimeSlot nextTimeSlot = getAvailableTimeSlot(timeSlot.getEndTime(),timeSlot.getTutor().getId());
            nextTimeSlot.updateIsAvailable();
        }

    }

    //특정 학생 수업 목록 조회 메소드
    private List<Lesson> findLessonByStudentId(Long studentId) {
        List<Lesson> lessons = lessonRepository.findAllByStudentId(studentId);
        //신청 수업 유무 확인
        if(lessons.isEmpty()){
            throw new NotFoundResourceException(ErrorCode.NOTFOUND_LESSONS);
        }
        return lessons;
    }

    private Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_STUDENT));
    }

    //예약 가능 timeSlot 조회 메서드
    private TimeSlot getAvailableTimeSlot(LocalDateTime startTime, Long tutorId) {
        TimeSlot timeSlot=timeSlotRepository.findByStartTimeAndTutorId(startTime, tutorId);
        //timeSlot의 예약 여부 확인
        if(!timeSlot.isAvailable()){
            throw new TimeSlotNotAvailableException(ErrorCode.ALREADY_BOOKING_TIMESLOT);
        }
        return timeSlot;
    }
}
