package com.example.jerryservice.Exception;


import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


// Handle validation
@RestControllerAdvice
public class ApiException {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<Map<String, String>> ErrorDetailResponse = new ArrayList<>();
        e.getFieldErrors().forEach(fieldError -> {
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("fieldError", fieldError.getField());
            errorDetail.put("Reason", fieldError.getDefaultMessage());
            errorDetail.put("RejectedValue", Objects.requireNonNull(fieldError.getRejectedValue()).toString());
            ErrorDetailResponse.add(errorDetail);
        });
        ErrorDetailsResponse<?> errorDetailsResponse = ErrorDetailsResponse.builder().code(HttpStatus.BAD_REQUEST.getReasonPhrase()).title(e.getTitleMessageCode()).details(ErrorDetailResponse).build();
        return new ResponseEntity<>(ErrorResponse.builder().error(errorDetailsResponse).build(), e.getStatusCode());
    }


    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> handleMethodRuntimeException(ResponseStatusException e) {

        ErrorDetailsResponse<?> errorDetailsResponse = ErrorDetailsResponse.builder().code(e.getTitleMessageCode()).title(e.getMessage()).details(e.getDetailMessageCode()).build();

        return new ResponseEntity<>(ErrorResponse.builder().error(errorDetailsResponse).build(), e.getStatusCode());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<?> ConstraintViolationException(ConstraintViolationException e) {
        ErrorDetailsResponse<?> errorDetailsResponse =
                ErrorDetailsResponse.builder()
                        .code("ConstraintViolation Erro")
                        .title("Validation Error")
                        .details(e.getMessage()).build();

        return new ResponseEntity<>(ErrorResponse.builder().error(errorDetailsResponse).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<?> MaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        ErrorDetailsResponse<?> errorDetailsResponse =
                ErrorDetailsResponse.builder()
                        .code(e.getStatusCode().toString())
                        .title(e.getTitleMessageCode())
                        .details(e.getDetailMessageCode()).build();

        return new ResponseEntity<>(ErrorResponse.builder().error(errorDetailsResponse).build(), HttpStatus.PAYLOAD_TOO_LARGE);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> IllegalArgumentException(IllegalArgumentException e) {
        ErrorDetailsResponse<?> errorDetailsResponse =
                ErrorDetailsResponse.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .title(e.getLocalizedMessage())
                        .details(e.getMessage()).build();

        return new ResponseEntity<>(ErrorResponse.builder().error(errorDetailsResponse).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }




}
