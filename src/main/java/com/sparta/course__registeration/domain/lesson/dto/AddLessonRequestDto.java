package com.sparta.course__registeration.domain.lesson.dto;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddLessonRequestDto {

    private Long studentId;
    private Long timeSlotId;
    private ClassPath classPath;

}

