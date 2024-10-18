package com.sparta.course__registeration.domain.timeslot.controller;

import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.service.TimeSlotService;
import com.sparta.course__registeration.global.exception.handler.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}
