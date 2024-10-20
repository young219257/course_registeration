package com.sparta.course__registeration.global.exception.handler;


import jakarta.persistence.EntityNotFoundException;

public class NotFoundResourceException extends EntityNotFoundException {
    private final ErrorCode errorCode;
    public NotFoundResourceException(final ErrorCode errorCode) {

        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
