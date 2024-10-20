package com.sparta.course__registeration.domain.timeslot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSlotRequestDto {

    private List<LocalDateTime> availableTimeSlots;
}
