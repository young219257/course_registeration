package com.sparta.course__registeration.domain.timeslot.controller;

import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotResponseDto;
import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.service.TimeSlotService;
import com.sparta.course__registeration.global.exception.handler.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;


    @PostMapping("/tutors/{tutorId}/timeslots")
    public ApiResponse addTimeSlot(@PathVariable Long tutorId, @RequestBody TimeSlotRequestDto timeSlotRequestDto) {

        timeSlotService.addTimeSlot(tutorId,timeSlotRequestDto);
        return ApiResponse.ok(200,"시간대 생성 성공");
    }

    @DeleteMapping("/tutors/{tutorId}/timeslots")
    public ApiResponse deleteTimeSlot(@PathVariable Long tutorId, @RequestBody DeleteTimeSlotRequestDto deleteTimeSlotRequestDto) {
        timeSlotService.deleteTimeSlot(deleteTimeSlotRequestDto);
        return ApiResponse.ok(200,"시간대 삭제 성공");
    }

    //수업 가능 시간대 조회
    @GetMapping("/api/lessons/timeslots")
    public ApiResponse<List<AvailableTimeslotResponseDto>> getAvailableTimeSlots(@RequestBody AvailableTimeslotRequestDto availableTimeslotRequestDto) {
        List<AvailableTimeslotResponseDto> availableTimeslotResponseDtoList=timeSlotService.getAvailableTimeSlots(availableTimeslotRequestDto);
        return ApiResponse.ok(200,"수업 가능 시간대 조회 성공",availableTimeslotResponseDtoList);
    }



}
