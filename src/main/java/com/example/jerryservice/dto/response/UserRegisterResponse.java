package com.example.jerryservice.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private String username;
    private String phoneNumber;
    private String displayName;
    private String chatId;
    private Boolean allowNotification;
}
