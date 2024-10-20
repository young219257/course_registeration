package com.sparta.course__registeration.domain.timeslot.service;

import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotResponseDto;
import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.global.exception.handler.dto.ApiResponse;

import java.util.List;

public interface TimeSlotService {
    List<TimeSlot> addTimeSlot(Long tutorId, TimeSlotRequestDto timeSlotRequestDto);

    TimeSlot deleteTimeSlot(Long tutorId, DeleteTimeSlotRequestDto deleteTimeSlotRequestDto);

    List<AvailableTimeslotResponseDto> getAvailableTimeSlots(AvailableTimeslotRequestDto availableTimeslotRequestDto);
}
