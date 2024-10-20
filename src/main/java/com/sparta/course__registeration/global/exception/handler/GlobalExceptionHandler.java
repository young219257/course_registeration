package com.sparta.course__registeration.global.exception.handler;

import com.sparta.course__registeration.global.exception.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception ex) {
        RestApiException restApiException=new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(restApiException, HttpStatus.INTERNAL_SERVER_ERROR);}

    @ExceptionHandler(NotFoundResourceException.class)
    public ResponseEntity notFoundResourceException(NotFoundResourceException ex) {
        RestApiException restApiException=new RestApiException(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(restApiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TimeSlotDeletionException.class)
    public ResponseEntity timeSlotDeletionException(TimeSlotDeletionException ex) {
        RestApiException restApiException=new RestApiException(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeSlotAlreadyExistsException.class)
    public ResponseEntity timeSlotAlreadyExistsException(TimeSlotAlreadyExistsException ex) {
        RestApiException restApiException=new RestApiException(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(restApiException, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TimeSlotNotAvailableException.class)
    public ResponseEntity timeSlotNotAvailableException(TimeSlotNotAvailableException ex) {
        RestApiException restApiException=new RestApiException(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

}