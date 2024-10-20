package com.sparta.course__registeration.domain.tutor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.tutor.dto.TutorRequestDto;
import com.sparta.course__registeration.domain.tutor.service.TutorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TutorController.class)
class TutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TutorService tutorService;


    @DisplayName("시간대 & 수업 길이로 수업 가능한 튜터 조회 - 성공")
    @Test
    void getAvailableTutors() throws Exception {
        //Given
        TutorRequestDto requestDto = TutorRequestDto.builder()
                .timeSlot(LocalDateTime.of(2024, 7, 12, 5, 0, 0))
                .classPath(ClassPath.SIXTY)
                .build();
        // When
        mockMvc.perform(get("/api/lessons/tutors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // Then
                .andDo(print())
                .andExpect(status().isOk());

    }
}