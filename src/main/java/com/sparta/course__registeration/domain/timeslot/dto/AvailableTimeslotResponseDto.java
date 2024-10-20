package com.sparta.course__registeration.domain.timeslot.dto;

import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeslotResponseDto {

    private String availableTimeSlot;

    public static AvailableTimeslotResponseDto from(TimeSlot timeSlot) {
        // ISO 8601 형식으로 변환
        String formattedTimeSlot = timeSlot.getStartTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")); // 원하는 포맷 지정
        return AvailableTimeslotResponseDto.builder()
                .availableTimeSlot(formattedTimeSlot)
                .build();
    }
}

