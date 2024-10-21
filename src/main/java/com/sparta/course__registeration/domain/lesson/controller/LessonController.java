package com.sparta.course__registeration.domain.lesson.controller;

import com.sparta.course__registeration.domain.lesson.dto.AddLessonRequestDto;
import com.sparta.course__registeration.domain.lesson.dto.GetLessonsRequestDto;
import com.sparta.course__registeration.domain.lesson.dto.LessonResponseDto;
import com.sparta.course__registeration.domain.lesson.service.LessonService;
import com.sparta.course__registeration.global.exception.handler.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    //수강 신청 api
    @PostMapping("/apply")
    public ApiResponse signUpLesson(@RequestBody AddLessonRequestDto addLessonRequestDto) {

        lessonService.signUpLesson(addLessonRequestDto);
        return ApiResponse.ok(200,"수강 신청 성공");

    }

    //신청한 수업 조회 api
    @GetMapping("/my-lessons")
    public ApiResponse<List<LessonResponseDto>> getAllLessons(@RequestBody GetLessonsRequestDto getLessonsRequestDto) {

        List<LessonResponseDto> lessonResponseDtos=lessonService.getAllLessons(getLessonsRequestDto);
        return ApiResponse.ok(200,"신청한 수업 조회 성공",lessonResponseDtos);

    }
}
