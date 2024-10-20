package com.sparta.course__registeration.global.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //Lessons 관련
    NOTFOUND_LESSONS(HttpStatus.NOT_FOUND, "신청한 수업이 없습니다."),
    //Student 관련
    NOTFOUND_STUDENT(HttpStatus.NOT_FOUND, "존재하지 않는 학생입니다"),


    //TimeSlot 관련
    NOTFOUND_TIMESLOT(HttpStatus.NOT_FOUND,"존재하지 않는 시간대입니다."),
    CANNOT_DELETE_BOOKED_TIMESLOT(HttpStatus.BAD_REQUEST,"해당 시간대는 이미 예약되어 삭제할 수 없습니다."),
    DUPLICATE_TIMESLOT(HttpStatus.CONFLICT,"이미 생성된 시간대입니다."),
    ALREADY_BOOKING_TIMESLOT(HttpStatus.BAD_REQUEST,"이미 예약된 시간대입니다."),
    NOT_AVAILABLE_TIMESLOT(HttpStatus.NOT_FOUND,"사용할 수 없는 시간대입니다."),


    //tutor 관련
    NOT_AVAILABLE_TUTORS(HttpStatus.NOT_FOUND, "수업 가능한 튜터가 없습니다."),
    NOTFOUND_TUTOR(HttpStatus.NOT_FOUND, "존재하지 않는 튜터입니다.");
    private final HttpStatus status;
    private final String message;


}
