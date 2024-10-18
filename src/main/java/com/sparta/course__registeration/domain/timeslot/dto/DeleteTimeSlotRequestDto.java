package com.sparta.course__registeration.domain.timeslot.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteTimeSlotRequestDto {

    private Long timeSlotId;
}
