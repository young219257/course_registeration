package com.sparta.course__registeration.domain.timeslot.dto;

import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeslotResponseDto {
    private LocalDateTime availableTimeslots;

    public static AvailableTimeslotResponseDto from(TimeSlot timeSlots) {
        return AvailableTimeslotResponseDto.builder().
                availableTimeslots(timeSlots.getStartTime()).build();
    }
}
