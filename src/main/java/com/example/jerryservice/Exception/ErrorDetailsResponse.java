package com.example.jerryservice.Exception;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorDetailsResponse<T> {
    private String code;
    private String title;
    private T details;

}
