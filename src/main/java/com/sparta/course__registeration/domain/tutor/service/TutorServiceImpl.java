package com.sparta.course__registeration.domain.tutor.service;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
import com.sparta.course__registeration.domain.tutor.dto.TutorRequestDto;
import com.sparta.course__registeration.domain.tutor.dto.TutorResponseDto;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.global.common.Utils;
import com.sparta.course__registeration.global.exception.handler.ErrorCode;
import com.sparta.course__registeration.global.exception.handler.NotFoundResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorServiceImpl implements TutorService {

    private final TimeSlotRepository timeSlotRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TutorResponseDto> getAvailableTutors(TutorRequestDto tutorRequestDto) {

        List<TimeSlot> timeSlots=findAllTimeSlot(tutorRequestDto.getTimeSlot()); //해당 시간대 목록 반환
        List<Tutor> availableTutors=findAvailableTutors(timeSlots,tutorRequestDto.getClassPath()); //수업 가능 튜터 반환

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

            // 현재 슬롯이 사용 가능한지 확인
            if (Utils.isAvailableForClass(timeSlot, timeSlots, classPath)) {
                availableTutors.add(tutor);
            }
        }
        //수업 가능한 튜터 유무 확인
        if (availableTutors.isEmpty()) {
            throw new NotFoundResourceException(ErrorCode.NOT_AVAILABLE_TUTORS);
        }

        // 중복된 튜터를 제거하고 반환
        return availableTutors.stream().distinct().collect(Collectors.toList());
    }

    //시간대에 해당하는 timeSlot 목록을 조회하는 메서드
    private List<TimeSlot> findAllTimeSlot(LocalDateTime startTime) {

        List<TimeSlot> timeSlots = timeSlotRepository.findAllByStartTimeBetween(startTime, startTime.plusMinutes(30));
        //해당하는 시간대 유무 확인
        if(timeSlots.isEmpty()){
            throw new NotFoundResourceException(ErrorCode.NOT_AVAILABLE_TIMESLOT);
        }
        return timeSlots;
    }
}
