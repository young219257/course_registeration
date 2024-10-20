package com.sparta.course__registeration.global.exception.handler;

public class TimeSlotNotAvailableException extends RuntimeException {
    private final ErrorCode errorCode;

    public TimeSlotNotAvailableException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
