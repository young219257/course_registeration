package com.sparta.course__registeration.domain.timeslot.dto;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimeslotRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private ClassPath classPath;
}
