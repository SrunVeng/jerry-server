package com.example.jerryservice.dto.request;



import lombok.Data;

@Data
public class GuestAuthRequest {
    private String displayName;
    private String uuid;
}
