package com.sparta.course__registeration.domain.timeslot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.service.TimeSlotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TimeSlotController.class)
class TimeSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TimeSlotService timeSlotService;


    @DisplayName("시간대 생성 - 성공")
    @Test
    void addTimeSlot() throws Exception {
        // Given
        Long tutorId = 1L;
        TimeSlotRequestDto requestDto = TimeSlotRequestDto.builder()
                .availableTimeslots(List.of(
                        LocalDateTime.of(2024, 7, 12, 5, 0, 0),
                        LocalDateTime.of(2024, 7, 12, 5, 30, 0)
                ))
                .build();

        // When
        mockMvc.perform(post("/api/tutors/{tutorId}/timeslots", tutorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                //Then
                .andExpect(status().isOk());
    }

    @DisplayName("시간대 삭제 - 성공")
    @Test
    void deleteTimeSlot() throws Exception {
        // Given
        Long tutorId = 1L;
        DeleteTimeSlotRequestDto requestDto = DeleteTimeSlotRequestDto.builder()
                .timeSlot(LocalDateTime.of(2024, 7, 12, 5, 0, 0))
                .build();

        // When
        mockMvc.perform(delete("/api/tutors/{tutorId}/timeslots", tutorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // Then
                .andExpect(status().isOk());// 기대하는 상태 코드
    }


    @DisplayName("기간 & 수업 길이로 현재 수업 가능한 시간대를 조회")
    @Test
    void getAllTimeSlots() throws Exception {
        //Given
        AvailableTimeslotRequestDto requestDto = AvailableTimeslotRequestDto.builder()
                .startDate(LocalDate.of(2024, 7, 12))
                .endDate(LocalDate.of(2024, 7, 13))
                .classPath(ClassPath.SIXTY)
                .build();

        // When
        mockMvc.perform(get("/api/lessons/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                //Then
                .andDo(print())
                .andExpect(status().isOk());

    }


}