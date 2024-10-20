package com.sparta.course__registeration.domain.tutor.service;

import com.sparta.course__registeration.domain.tutor.dto.TutorRequestDto;
import com.sparta.course__registeration.domain.tutor.dto.TutorResponseDto;

import java.util.List;

public interface TutorService {
    List<TutorResponseDto> getAvailableTutors(TutorRequestDto tutorRequestDto);
}
