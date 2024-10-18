package com.sparta.course__registeration.domain.timeslot.service;

import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.domain.tutor.repository.TutorRepository;
import com.sparta.course__registeration.global.exception.handler.ErrorCode;
import com.sparta.course__registeration.global.exception.handler.NotFoundResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final TutorRepository tutorRepository;

    //시간대 생성
    @Override
    @Transactional(readOnly = true)
    public void addTimeSlot(Long tutorId, TimeSlotRequestDto timeSlotRequestDto) {

        Tutor tutor = findTutorById(tutorId);
        List<TimeSlot> timeSlots=new ArrayList<>();

        for(LocalDateTime availableTimeSlot : timeSlotRequestDto.getAvailableTimeslots()){

            TimeSlot timeSlot=TimeSlot.of(tutor,availableTimeSlot);
            timeSlots.add(timeSlot);

        }
        timeSlotRepository.saveAll(timeSlots);


    }

    //시간대 삭제
    @Override
    @Transactional
    public void deleteTimeSlot(DeleteTimeSlotRequestDto deleteTimeSlotRequestDto) {
        TimeSlot timeSlot = findTimeSlotById(deleteTimeSlotRequestDto.getTimeSlotId());
        timeSlotRepository.delete(timeSlot);
    }

    private Tutor findTutorById(Long tutorId){
        return tutorRepository.findById(tutorId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_TUTOR));
    }

    private TimeSlot findTimeSlotById(Long timeSlotId){
        return timeSlotRepository.findById(timeSlotId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_TIMESLOT));
    }
}
