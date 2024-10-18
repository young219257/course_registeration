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
import com.sparta.course__registeration.global.exception.handler.ErrorCode;
import com.sparta.course__registeration.global.exception.handler.NotFoundResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final TimeSlotRepository timeSlotRepository;


    @Override
    public void signUpLesson(AddLessonRequestDto addLessonRequestDto) {

        Student student=findStudentById(addLessonRequestDto.getStudentId());
        TimeSlot timeSlot=findTimeSlotById(addLessonRequestDto.getTimeSlotId());
        ClassPath classPath=addLessonRequestDto.getClassPath();

        Lesson lesson=Lesson.of(classPath, timeSlot.getTutor(), student,timeSlot);
        lessonRepository.save(lesson);

        if(classPath.equals(60)){
            //timeSlot의 endTime이 startTime이며 tutor가 동일한 TimeSlot을 찾아 lesson에 추가
            TimeSlot nextTimeSlot = findTimeSlotByStartTimeAndTutor(timeSlot.getEndTime(),timeSlot.getTutor());
            Lesson nextLesson =Lesson.of(classPath, nextTimeSlot.getTutor(), student,nextTimeSlot);
            lessonRepository.save(nextLesson);
        }

    }

    @Override
    public List<LessonResponseDto> getAllLessons(GetLessonsRequestDto getLessonsRequestDto) {

        List<Lesson> lessons=lessonRepository.findAllByStudentId(getLessonsRequestDto.getStudentId());
        List<LessonResponseDto> lessonResponseDtos=new ArrayList<>();

        for(Lesson lesson:lessons){

            lessonResponseDtos.add(LessonResponseDto.from(lesson));
        }

        return lessonResponseDtos;
    }

    private Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_STUDENT));
    }

    private TimeSlot findTimeSlotById(Long timeSlotId) {
        return timeSlotRepository.findById(timeSlotId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_TIMESLOT));
    }

    private TimeSlot findTimeSlotByStartTimeAndTutor(LocalDateTime startTime, Tutor tutor) {
        return timeSlotRepository.findBystartTimeAndTutor(startTime,tutor);
    }
}
