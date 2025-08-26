package com.example.jerryservice.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginResponse {

    String tokenType;
    String accessToken;
    String refreshToken;
}
