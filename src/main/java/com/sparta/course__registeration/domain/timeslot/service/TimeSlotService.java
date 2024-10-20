package com.sparta.course__registeration.domain.timeslot.service;

import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotResponseDto;
import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;

import java.util.List;

public interface TimeSlotService {
    void addTimeSlot(Long tutorId, TimeSlotRequestDto timeSlotRequestDto);

    void deleteTimeSlot(Long tutorId, DeleteTimeSlotRequestDto deleteTimeSlotRequestDto);

    List<AvailableTimeslotResponseDto> getAvailableTimeSlots(AvailableTimeslotRequestDto availableTimeslotRequestDto);
}
