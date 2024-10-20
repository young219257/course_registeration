package com.sparta.course__registeration.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestApiException {

    private int statusCode;
    private String msg;
}