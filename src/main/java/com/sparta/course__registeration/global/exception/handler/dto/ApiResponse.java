package com.sparta.course__registeration.global.exception.handler.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {
    int statusCode;
    String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    T data;



    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static <T> ApiResponse<T> ok(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, message, data);
    }
    public static <T> ApiResponse<T> ok(int statusCode, String message) {
        return new ApiResponse<>(statusCode, message);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(statusCode, message);
    }
    public static <T> ApiResponse<T> error(int statusCode, String message,T data) {
        return new ApiResponse<>(statusCode, message,data);
    }

}