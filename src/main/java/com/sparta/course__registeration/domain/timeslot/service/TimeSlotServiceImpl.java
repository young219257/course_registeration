package com.sparta.course__registeration.domain.timeslot.service;

import com.sparta.course__registeration.domain.lesson.type.ClassPath;
import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.AvailableTimeslotResponseDto;
import com.sparta.course__registeration.domain.timeslot.dto.DeleteTimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.dto.TimeSlotRequestDto;
import com.sparta.course__registeration.domain.timeslot.entity.TimeSlot;
import com.sparta.course__registeration.domain.timeslot.repository.TimeSlotRepository;
import com.sparta.course__registeration.domain.tutor.entity.Tutor;
import com.sparta.course__registeration.domain.tutor.repository.TutorRepository;
import com.sparta.course__registeration.global.exception.handler.ErrorCode;
import com.sparta.course__registeration.global.exception.handler.NotFoundResourceException;
import com.sparta.course__registeration.global.exception.handler.TimeSlotAlreadyExistsException;
import com.sparta.course__registeration.global.exception.handler.TimeSlotDeletionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final TutorRepository tutorRepository;

    //시간대 생성
    @Override
    @Transactional
    public List<TimeSlot> addTimeSlot(Long tutorId, TimeSlotRequestDto timeSlotRequestDto) {

        Tutor tutor = findTutorByTutorId(tutorId);
        List<TimeSlot> timeSlots=new ArrayList<>();

        for(LocalDateTime availableTimeSlot : timeSlotRequestDto.getAvailableTimeSlots()){

            // 중복된 시간대가 존재하는지 확인
            if (timeSlotRepository.existsByTutorAndStartTime(tutor, availableTimeSlot)) {
                throw new TimeSlotAlreadyExistsException(ErrorCode.DUPLICATE_TIMESLOT);
            }

            TimeSlot timeSlot=TimeSlot.of(tutor,availableTimeSlot);
            timeSlots.add(timeSlot);

        }
        List<TimeSlot> savedTimeSlots = timeSlotRepository.saveAll(timeSlots);
        System.out.println("Saved TimeSlots: " + savedTimeSlots); // 저장 후 확인
        return savedTimeSlots;
    }

    //시간대 삭제
    @Override
    @Transactional
    public TimeSlot deleteTimeSlot(Long tutorId, DeleteTimeSlotRequestDto deleteTimeSlotRequestDto) {
        TimeSlot timeSlot = findTimeSlotByStartTimeAndTutorId(deleteTimeSlotRequestDto.getTimeSlot(),tutorId);

        // 시간대가 이미 예약되어 있을 경우 삭제 불가 예외 발생
        if (!timeSlot.isAvailable()) {
            throw new TimeSlotDeletionException(ErrorCode.CANNOT_DELETE_BOOKED_TIMESLOT);
        }
        timeSlotRepository.delete(timeSlot);
        return timeSlot;
    }

    //기간, 수업 길이로 시간대 조회
    @Override
    @Transactional(readOnly = true)
    public List<AvailableTimeslotResponseDto> getAvailableTimeSlots(AvailableTimeslotRequestDto availableTimeslotRequestDto) {

        List<TimeSlot> timeSlots = findTimeSlotByPeriod(availableTimeslotRequestDto); // 기간 내 시간대
        List<TimeSlot> availableTimeslots = filterAvailableTimeSlot(timeSlots, availableTimeslotRequestDto); // 이용 가능한 시간대

        if(availableTimeslots.isEmpty()){
            throw new NotFoundResourceException(ErrorCode.NOT_AVAILABLE_TIMESLOT);
        }
        // 중복 제거 및 시간 순으로 정렬 후 DTO 생성
        return availableTimeslots.stream()
                .distinct()
                .sorted(Comparator.comparing(TimeSlot::getStartTime)) // 시작 시간을 기준으로 정렬
                .map(AvailableTimeslotResponseDto::from) // from 메서드를 사용하여 DTO 생성
                .collect(Collectors.toList());
    }

    private Tutor findTutorByTutorId(Long tutorId){
        return tutorRepository.findById(tutorId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_TUTOR));
    }

    private TimeSlot findTimeSlotByStartTimeAndTutorId(LocalDateTime startTime, Long tutorId) {
        return timeSlotRepository.findByStartTimeAndTutorId(startTime,tutorId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_TIMESLOT));
    }


    private List<TimeSlot> findTimeSlotByPeriod(AvailableTimeslotRequestDto requestDto) {

        // 시작 시간 : 당일이면 현재 시간, 아니면 시작일의 00:00
        LocalDateTime startDateTime = requestDto.getStartDate().isEqual(LocalDate.now())
                ? LocalDateTime.now()
                : requestDto.getStartDate().atStartOfDay();

        // 종료 시간 : KST 기준으로 다음 날 00:00
        ZonedDateTime endDateTimeUtc = requestDto.getEndDate().atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime endDateTimeKst = endDateTimeUtc.withZoneSameInstant(ZoneId.of("Asia/Seoul")).plusDays(1);
        LocalDateTime endDateTime = endDateTimeKst.toLocalDate().atStartOfDay(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        return timeSlotRepository.findAllByStartTimeBetween(startDateTime, endDateTime);
    }

    private List<TimeSlot> filterAvailableTimeSlot(List<TimeSlot> timeSlots, AvailableTimeslotRequestDto availableTimeslotRequestDto) {
        List<TimeSlot> availableTimeslots = new ArrayList<>();

        for (TimeSlot timeSlot : timeSlots) {

            // 만약 동일한 startTime의 TimeSlot이 이미 존재하면 다음으로 넘김
            boolean alreadyExists = availableTimeslots.stream()
                    .anyMatch(existingSlot -> existingSlot.getStartTime().isEqual(timeSlot.getStartTime()));

            if (alreadyExists) {
                continue; // 다음 TimeSlot으로 넘어감
            }
            if (timeSlot.isAvailable()) { // 예약 가능한 시간대인지 확인
                if (availableTimeslotRequestDto.getClassPath().equals(ClassPath.SIXTY)) { // 수업 길이가 60분인 경우

                    boolean hasNextSlot = false;

                    // 다음 30분 슬롯 확인
                    for (TimeSlot nextSlot : timeSlots) {
                        if (nextSlot.getTutor().equals(timeSlot.getTutor())
                                && nextSlot.getStartTime().isEqual(timeSlot.getEndTime())) {
                            hasNextSlot = true;
                            break;
                        }
                    }

                    // 다음 타임에 시간이 존재할 때
                    if (hasNextSlot) {
                        availableTimeslots.add(timeSlot);
                    }
                }

                //classPath 30분인 경우
                availableTimeslots.add(timeSlot);
            }
        }

        //중복된 시간대 제거하고 반환
        return availableTimeslots.stream().distinct().collect(Collectors.toList());
    }


}
