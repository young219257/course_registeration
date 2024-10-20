package com.sparta.course__registeration.domain.timeslot.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteTimeSlotRequestDto {

    private LocalDateTime timeSlot;
}
