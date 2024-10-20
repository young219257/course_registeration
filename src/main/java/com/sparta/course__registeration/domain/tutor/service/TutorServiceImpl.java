package com.sparta.course__registeration.domain.tutor.service;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
import com.sparta.course__registeration.domain.tutor.dto.TutorRequestDto;
import com.sparta.course__registeration.domain.tutor.dto.TutorResponseDto;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.global.exception.handler.ErrorCode;
import com.sparta.course__registeration.global.exception.handler.NotFoundResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TutorServiceImpl implements TutorService {

    private final TimeSlotRepository timeSlotRepository;

    @Override
    @Transactional
    public List<TutorResponseDto> getAvailableTutors(TutorRequestDto tutorRequestDto) {

        List<TimeSlot> timeSlot=findAllTimeSlot(tutorRequestDto.getTimeSlot());

        List<Tutor> availableTutors=findAvailableTutors(timeSlot,tutorRequestDto.getClassPath());

        return availableTutors.stream()
                .map(TutorResponseDto::from)
                .collect(Collectors.toList());

    }

    // 주어진 TimeSlot 리스트에서 가능한 튜터를 찾음
    private List<Tutor> findAvailableTutors(List<TimeSlot> timeSlots, ClassPath classPath) {

        List<Tutor> availableTutors = new ArrayList<>();

        // 선택한 시간대에 해당하는 모든 TimeSlot에서 튜터 확인
        for (TimeSlot timeSlot : timeSlots) {
            Tutor tutor = timeSlot.getTutor();

            // 1. classPath = 60인 경우: 다음 시간대도 확인
            if (classPath.equals(ClassPath.SIXTY)) {

                TimeSlot nextSlot = findNextTimeSlotBystartTimeAndTutor(timeSlot.getEndTime(),tutor);

                if (timeSlot.isAvailable() && nextSlot.isAvailable()) {
                    availableTutors.add(tutor);
                }
            }
            // 2. classPath = 30인 경우: 현재 시간대만 확인
            else if (classPath.equals(ClassPath.THIRTY)) {
                if (timeSlot.isAvailable()) {
                    availableTutors.add(tutor);
                }
            }
        }

        // 중복된 튜터를 제거하고 반환
        return availableTutors.stream().distinct().collect(Collectors.toList());
    }


    private TimeSlot findNextTimeSlotBystartTimeAndTutor(LocalDateTime startTime, Tutor tutor) {
        return timeSlotRepository.findByStartTimeAndTutorId(startTime, tutor.getId()).orElseThrow(()-> new NotFoundResourceException(ErrorCode.NOTFOUND_TIMESLOT));
    }

    private List<TimeSlot> findAllTimeSlot(LocalDateTime timeSlot) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAllByStartTimeBetween(timeSlot, timeSlot.plusMinutes(30));
        // 로그 출력
        log.info("Found time slots: {}", timeSlots);
        return timeSlots;
    }
}
