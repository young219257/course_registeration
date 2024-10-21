package com.sparta.course__registeration.domain.tutor.service;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
import com.sparta.course__registeration.domain.tutor.dto.TutorRequestDto;
import com.sparta.course__registeration.domain.tutor.dto.TutorResponseDto;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.domain.tutor.repository.TutorRepository;
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
public class TutorServiceImpl implements TutorService {

    private final TimeSlotRepository timeSlotRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TutorResponseDto> getAvailableTutors(TutorRequestDto tutorRequestDto) {

        List<TimeSlot> timeSlot=findAllTimeSlot(tutorRequestDto.getTimeSlot()); //해당 시간대 목록 반환
        //해당하는 시간대 유무 확인
        if(timeSlot.isEmpty()){
            throw new NotFoundResourceException(ErrorCode.NOT_AVAILABLE_TIMESLOT);
        }

        List<Tutor> availableTutors=findAvailableTutors(timeSlot,tutorRequestDto.getClassPath()); //수업 가능 튜터 반환

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

            // 1. classPath = 60인 경우: 연속한 다음 시간대 유무 확인
            if (classPath.equals(ClassPath.SIXTY)) {

                TimeSlot nextSlot = findNextTimeSlotBystartTimeAndTutor(timeSlot.getEndTime(),tutor.getId());
                // 1) 연속한 시간대 없는 경우
                if(nextSlot==null){
                    continue; //다음 timeSlot으로 넘김
                }
                // 2) 해당 시간대, 다음 시간대의 예약 여부 확인
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
        //수업 가능한 튜터 유무 확인
        if (availableTutors.isEmpty()) {
            throw new NotFoundResourceException(ErrorCode.NOT_AVAILABLE_TUTORS);
        }

        // 중복된 튜터를 제거하고 반환
        return availableTutors.stream().distinct().collect(Collectors.toList());
    }

    //연속한 다음 timeSlot을 조회하는 메서드
    private TimeSlot findNextTimeSlotBystartTimeAndTutor(LocalDateTime startTime,Long tutorId) {
        return timeSlotRepository.findByStartTimeAndTutorId(startTime, tutorId);
    }
    //시간대에 해당하는 timeSlot 목록을 조회하는 메서드
    private List<TimeSlot> findAllTimeSlot(LocalDateTime startTime) {
        return timeSlotRepository.findAllByStartTimeBetween(startTime, startTime.plusMinutes(30));
    }
}
