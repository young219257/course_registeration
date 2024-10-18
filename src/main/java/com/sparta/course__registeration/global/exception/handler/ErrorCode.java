package com.sparta.course__registeration.global.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //tutor 관련
    NOTFOUND_TUTOR(HttpStatus.NOT_FOUND, "존재하지 않는 튜터입니다."),
    NOTFOUND_TIMESLOT(HttpStatus.NOT_FOUND,"존재하지 않는 시간대입니다.");
    private final HttpStatus status;
    private final String message;
}
