package com.sparta.course__registeration.domain.timeslot.service;

import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;

public interface TimeSlotService {
    void addTimeSlot(Long tutorId, TimeSlotRequestDto timeSlotRequestDto);

    void deleteTimeSlot(DeleteTimeSlotRequestDto deleteTimeSlotRequestDto);
}
