package com.sparta.course__registeration.global.exception.handler;

public class TimeSlotAlreadyExistsException extends RuntimeException {

    private final  ErrorCode errorCode;

    public TimeSlotAlreadyExistsException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}