package com.example.jerryservice.service;

import com.example.jerryservice.dto.request.*;
import com.example.jerryservice.dto.response.UserLoginResponse;
import com.example.jerryservice.dto.response.UserRegisterResponse;
import com.example.jerryservice.dto.response.GuestResponse;

import java.util.List;

public interface AuthService {
    UserRegisterResponse userRegister(UserRegisterRequest request);

    GuestResponse loginAsGuest(GuestAuthRequest request);

    UserLoginResponse userLogin(UserLoginRequest request);

    UserLoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    List<UserRegisterResponse> getAll();

    void delete(Long id);

    void update(UserUpdateRequest req);
}
