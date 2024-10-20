package com.sparta.course__registeration.domain.lesson.service;

import com.sparta.course__registeration.domain.lesson.dto.AddLessonRequestDto;
import com.sparta.course__registeration.domain.lesson.dto.GetLessonsRequestDto;
import com.sparta.course__registeration.domain.lesson.dto.LessonResponseDto;

import java.util.List;

public interface LessonService {

    void signUpLesson(AddLessonRequestDto addLessonRequestDto);

    List<LessonResponseDto> getAllLessons(GetLessonsRequestDto getLessonsRequestDto);
}
