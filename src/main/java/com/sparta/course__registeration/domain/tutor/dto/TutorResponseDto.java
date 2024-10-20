package com.sparta.course__registeration.domain.tutor.dto;

import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorResponseDto {

    private Long tutorId;
    private String tutorName;

    public static TutorResponseDto from(Tutor tutor) {
        return TutorResponseDto.builder()
                .tutorId(tutor.getId())
                .tutorName(tutor.getName())
                .build();
    }
}
