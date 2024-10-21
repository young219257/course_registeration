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
import com.sparta.course__registeration.global.common.Utils;
import java.time.*;
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
        List<TimeSlot> availableTimeslots = filterAvailableTimeSlot(timeSlots, availableTimeslotRequestDto.getClassPath()); // 이용 가능한 시간대 반환


        // 중복 제거 및 시간 순으로 정렬
        return availableTimeslots.stream()
                .distinct()
                .sorted(Comparator.comparing(TimeSlot::getStartTime)) // 시작 시간을 기준으로 정렬
                .map(AvailableTimeslotResponseDto::from)
                .collect(Collectors.toList());
    }

    //기간 내 timeSlot을 조회하는 메서드
    private List<TimeSlot> findTimeSlotByPeriod(AvailableTimeslotRequestDto requestDto) {

        // 시작 시간 : 당일이면 현재 시간 기준, 아니면 시작일의 00:00 기준
        LocalDateTime startDateTime = requestDto.getStartDate().isEqual(LocalDate.now())
                ? LocalDateTime.now()
                : requestDto.getStartDate().atStartOfDay();

        // 종료 시간 : KST 기준으로 다음 날 00:00
        LocalDateTime endDateTime = calculateEndDateTime(requestDto);

        return timeSlotRepository.findAllByStartTimeBetween(startDateTime, endDateTime);
    }

    //기간 내 timeSlot 중 이용 가능한 시간대를 필터링하는 메서드
    private List<TimeSlot> filterAvailableTimeSlot(List<TimeSlot> timeSlots, ClassPath classPath) {
        List<TimeSlot> availableTimeslots = new ArrayList<>();

        for (TimeSlot timeSlot : timeSlots) {

            // 중복된 시간대 여부 확인
            if (isDuplicateSlot(timeSlot, availableTimeslots)) {
                continue; // 다음 슬롯으로 넘어감
            }

            // 현재 슬롯이 사용 가능한지 확인
            if (Utils.isAvailableForClass(timeSlot, timeSlots, classPath)) {
                availableTimeslots.add(timeSlot);
            }
        }
        //이용가능한 시간대의 유무 확인
        if(availableTimeslots.isEmpty()){
            throw new NotFoundResourceException(ErrorCode.NOT_AVAILABLE_TIMESLOT);
        }

        return availableTimeslots.stream().distinct().collect(Collectors.toList());
    }

    // availableTimeSlot 기준 중복된 시간대 확인
    private boolean isDuplicateSlot(TimeSlot timeSlot, List<TimeSlot> availableTimeslots) {
        for (TimeSlot existingSlot : availableTimeslots) {
            if (existingSlot.getStartTime().isEqual(timeSlot.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    // 종료 시간 계산: UTC에서 KST로 변환 후 다음 날 00:00
    private LocalDateTime calculateEndDateTime(AvailableTimeslotRequestDto requestDto) {
        ZonedDateTime endDateUtc = requestDto.getEndDate().atStartOfDay(ZoneId.of("UTC"));
        ZonedDateTime endDateKst = endDateUtc.withZoneSameInstant(ZoneId.of("Asia/Seoul")).plusDays(1);
        return endDateKst.toLocalDate().atStartOfDay();
    }

    private Tutor findTutorByTutorId(Long tutorId){
        return tutorRepository.findById(tutorId).orElseThrow(()->new NotFoundResourceException(ErrorCode.NOTFOUND_TUTOR));
    }

    //시작시간, 튜터Id로 timeSlot 반환
    private TimeSlot findTimeSlotByStartTimeAndTutorId(LocalDateTime startTime, Long tutorId) {
        TimeSlot timeSlot= timeSlotRepository.findByStartTimeAndTutorId(startTime, tutorId);
        // 존재하는 시간대인지 확인
        if(timeSlot==null){
            throw new NotFoundResourceException(ErrorCode.NOTFOUND_TIMESLOT);
        }
        return timeSlot;
    }




}
