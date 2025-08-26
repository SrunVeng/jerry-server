package com.example.jerryservice.service;

import com.example.jerryservice.dto.request.GuestAuthRequest;
import com.example.jerryservice.dto.request.RefreshTokenRequest;
import com.example.jerryservice.dto.request.UserLoginRequest;
import com.example.jerryservice.dto.request.UserRegisterRequest;
import com.example.jerryservice.dto.response.UserLoginResponse;
import com.example.jerryservice.dto.response.UserRegisterResponse;
import com.example.jerryservice.dto.response.GuestResponse;

public interface AuthService {
    UserRegisterResponse userRegister(UserRegisterRequest request);

    GuestResponse loginAsGuest(GuestAuthRequest request);

    UserLoginResponse userLogin(UserLoginRequest request);

    UserLoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
