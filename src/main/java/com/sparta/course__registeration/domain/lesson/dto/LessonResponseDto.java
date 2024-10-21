package com.sparta.course__registeration.domain.lesson.dto;

import com.sparta.course__registeration.domain.lesson.entity.Lesson;
import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponseDto {

    private Long lessonId;
    private String tutorName;
    private String timeSlot;
    private ClassPath classPath;

    public static LessonResponseDto from(Lesson lesson) {
        // ISO 8601 형식으로 출력
        String formattedTimeSlot = lesson.getTimeslot().getStartTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        return LessonResponseDto.builder()
                .lessonId(lesson.getId())
                .tutorName(lesson.getTutor().getName())
                .timeSlot(formattedTimeSlot)
                .classPath(lesson.getClassPath())
                .build();
    }

}
