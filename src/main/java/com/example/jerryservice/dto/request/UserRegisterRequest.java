package com.example.jerryservice.dto.request;



import lombok.Data;



@Data
public class UserRegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String displayName;
    // Push messaging
    private String chatId;                           // set when user talks to bot
    private Boolean allowNotification;             // user consent (default false)
}
