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

            // 중복된 시간대가 존재 여부 확인
            if (timeSlotRepository.existsByTutorAndStartTime(tutor, availableTimeSlot)) {
                throw new TimeSlotAlreadyExistsException(ErrorCode.DUPLICATE_TIMESLOT);
            }

            TimeSlot timeSlot=TimeSlot.of(tutor,availableTimeSlot);
            timeSlots.add(timeSlot);

        }
        return timeSlotRepository.saveAll(timeSlots);
    }

    //시간대 삭제
    @Override
    @Transactional
    public TimeSlot deleteTimeSlot(Long tutorId, DeleteTimeSlotRequestDto deleteTimeSlotRequestDto) {
        TimeSlot timeSlot = findTimeSlotByStartTimeAndTutorId(deleteTimeSlotRequestDto.getTimeSlot(),tutorId);

        // 존재하는 시간대인지 확인
        if(timeSlot==null){
            throw new NotFoundResourceException(ErrorCode.NOTFOUND_TIMESLOT);
        }

        // 이미 예약된 시간대인지 확인
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

        List<TimeSlot> timeSlots = findTimeSlotByPeriod(availableTimeslotRequestDto); // 기간 내 시간대 반환
        List<TimeSlot> availableTimeslots = filterAvailableTimeSlot(timeSlots, availableTimeslotRequestDto); // 이용 가능한 시간대 반환

        //이용가능한 시간대의 유무 확인
        if(availableTimeslots.isEmpty()){
            throw new NotFoundResourceException(ErrorCode.NOT_AVAILABLE_TIMESLOT);
        }

        // 중복 제거 및 시간 순으로 정렬
        return availableTimeslots.stream()
                .distinct()
                .sorted(Comparator.comparing(TimeSlot::getStartTime)) // 시작 시간을 기준으로 정렬
                .map(AvailableTimeslotResponseDto::from)
                .collect(Collectors.toList());
    }

    private Tutor findTutorByTutorId(Long tutorId){
        return tutorRepository.findById(tutorId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_TUTOR));
    }

    //시작시간, 튜터Id로 timeSlot 반환
    private TimeSlot findTimeSlotByStartTimeAndTutorId(LocalDateTime startTime, Long tutorId) {
        return timeSlotRepository.findByStartTimeAndTutorId(startTime, tutorId);
    }

    //기간 내 timeSlot을 조회하는 메서드
    private List<TimeSlot> findTimeSlotByPeriod(AvailableTimeslotRequestDto requestDto) {

        // 시작 시간 : 당일이면 현재 시간 기준, 아니면 시작일의 00:00 기준
        LocalDateTime startDateTime = requestDto.getStartDate().isEqual(LocalDate.now())
                ? LocalDateTime.now()
                : requestDto.getStartDate().atStartOfDay();

        // 종료 시간 : KST 기준으로 다음 날 00:00
        ZonedDateTime endDateTimeUtc = requestDto.getEndDate().atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime endDateTimeKst = endDateTimeUtc.withZoneSameInstant(ZoneId.of("Asia/Seoul")).plusDays(1);
        LocalDateTime endDateTime = endDateTimeKst.toLocalDate().atStartOfDay(ZoneId.of("Asia/Seoul")).toLocalDateTime();

        return timeSlotRepository.findAllByStartTimeBetween(startDateTime, endDateTime);
    }

    //기간 내 timeSlot 중 이용 가능한 시간대를 필터링하는 메서드
    private List<TimeSlot> filterAvailableTimeSlot(List<TimeSlot> timeSlots, AvailableTimeslotRequestDto availableTimeslotRequestDto) {
        List<TimeSlot> availableTimeslots = new ArrayList<>();

        for (TimeSlot timeSlot : timeSlots) {

            // availableTimeSlots에 동일한 시간대 존재 여부 확인
            boolean alreadyExists = availableTimeslots.stream()
                    .anyMatch(existingSlot -> existingSlot.getStartTime().isEqual(timeSlot.getStartTime()));

            if (alreadyExists) {
                continue; // 다음 TimeSlot으로 넘어감
            }

            if (timeSlot.isAvailable()) { // 예약 가능한 시간대인지 확인

                if (availableTimeslotRequestDto.getClassPath().equals(ClassPath.SIXTY)) { // 수업 길이가 60분인 경우

                    // 연속되는 TimeSlot의 유무 확인
                    boolean hasNextSlot = timeSlots.stream()
                            .anyMatch(nextSlot -> nextSlot.getTutor().equals(timeSlot.getTutor()) &&
                                    nextSlot.getStartTime().isEqual(timeSlot.getEndTime()) &&
                                    nextSlot.isAvailable());

                    // 두 슬롯이 연속되는 경우에만 추가
                    if (hasNextSlot) {
                        availableTimeslots.add(timeSlot);
                    }
                } else if (availableTimeslotRequestDto.getClassPath().equals(ClassPath.THIRTY)) {
                    // 30분 수업의 경우 바로 추가
                    availableTimeslots.add(timeSlot);
                }
            }
        }


        return availableTimeslots.stream().distinct().collect(Collectors.toList());
    }


}
