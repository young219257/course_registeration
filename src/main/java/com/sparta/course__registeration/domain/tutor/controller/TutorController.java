package com.sparta.course__registeration.domain.tutor.controller;

import com.sparta.course__registeration.domain.tutor.dto.TutorRequestDto;
import com.sparta.course__registeration.domain.tutor.dto.TutorResponseDto;
import com.sparta.course__registeration.domain.tutor.service.TutorService;
import com.sparta.course__registeration.global.exception.handler.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TutorController {

    private final TutorService tutorService;

    @GetMapping("/lessons/tutors")
    public ApiResponse<List<TutorResponseDto>> getAvailableTutors(@RequestBody TutorRequestDto tutorRequestDto) {
        List<TutorResponseDto> tutorResponseDtos=tutorService.getAvailableTutors(tutorRequestDto);
        return ApiResponse.ok(200,"수업 가능한 튜터 조회 성공", tutorResponseDtos);
    }
}
