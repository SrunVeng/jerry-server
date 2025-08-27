package com.example.jerryservice.dto.request;


import com.example.jerryservice.entity.RoleEntity;
import com.example.jerryservice.entity.UserEntity;
import lombok.Data;

import java.util.List;


@Data
public class UserUpdateRequest {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String displayName;
    private UserEntity.SourceType source;
    private String chatId;
    private Boolean allowNotification;
    private String roles;
    public enum SourceType {USER, GUEST}
}
