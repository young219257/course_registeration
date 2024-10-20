package com.sparta.course__registeration.domain.tutor.dto;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorRequestDto {

    private LocalDateTime timeSlot;
    private ClassPath classPath;

}
