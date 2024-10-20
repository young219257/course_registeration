package com.sparta.course__registeration.global.exception.handler;

public class TimeSlotDeletionException extends RuntimeException {

    private final ErrorCode errorCode;

    public TimeSlotDeletionException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}