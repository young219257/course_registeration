package com.sparta.course__registeration.domain.lesson.dto;

import com.sparta.course__registeration.domain.lesson.entity.Lesson;
import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponseDto {

    private Long lessonId;
    private String tutorName;
    private LocalDateTime timeSlot;
    private ClassPath classPath;

    public static LessonResponseDto from(Lesson lesson) {
        return LessonResponseDto.builder()
                .lessonId(lesson.getId())
                .tutorName(lesson.getTutor().getName())
                .timeSlot(lesson.getTimeslot().getStartTime())
                .classPath(lesson.getClassPath())
                .build();
    }

}
