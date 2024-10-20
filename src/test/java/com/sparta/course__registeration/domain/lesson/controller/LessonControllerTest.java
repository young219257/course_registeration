package com.sparta.course__registeration.domain.lesson.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.course__registeration.domain.lesson.dto.AddLessonRequestDto;
import com.sparta.course__registeration.domain.lesson.dto.GetLessonsRequestDto;
import com.sparta.course__registeration.domain.lesson.service.LessonService;
import com.sparta.course__registeration.domain.lesson.type.ClassPath;
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

@WebMvcTest(controllers = LessonController.class)
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LessonService lessonService;


    @DisplayName("수강신청 - 성공")
    @Test
    void signUpLesson() throws Exception {
        //Given
        Long studentId = 1L;
        Long tutorId = 2L;
        AddLessonRequestDto requestDto=AddLessonRequestDto.builder()
                .studentId(studentId)
                .timeSlot(LocalDateTime.of(2024, 7, 12, 5, 0, 0))
                .tutorId(tutorId)
                .classPath(ClassPath.SIXTY).build();

        // When
        mockMvc.perform(post("/api/lessons/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // Then
                .andExpect(status().isOk());

    }

    @DisplayName("신청한 수강 조회 - 성공")
    @Test
    void getAllLessons() throws Exception {
        //Given
        Long studentId = 1L;
        GetLessonsRequestDto requestDto=GetLessonsRequestDto.builder().studentId(studentId).build();

        // When
        mockMvc.perform(get("/api/lessons/my-lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // Then
                .andDo(print())
                .andExpect(status().isOk());
    }
}