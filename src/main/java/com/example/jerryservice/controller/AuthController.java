package com.example.jerryservice.controller;

import com.example.jerryservice.dto.request.*;
import com.example.jerryservice.dto.response.UserLoginResponse;
import com.example.jerryservice.dto.response.UserRegisterResponse;
import com.example.jerryservice.dto.response.GuestResponse;
import com.example.jerryservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/user/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse res = authService.userRegister(request);
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN')")
    @GetMapping("/user/getAll")
    public ResponseEntity<List<UserRegisterResponse>> getAll() {
        List<UserRegisterResponse> res = authService.getAll();
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/user/update")
    public ResponseEntity<Void> update(@RequestBody UserUpdateRequest req) {
        authService.update(req);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN')")
    @PostMapping("/user/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authService.delete(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/user/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse res = authService.userLogin(request);
        return ResponseEntity.ok(res);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/user/login/refresh-token")
    UserLoginResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/guest/login")
    public ResponseEntity<GuestResponse> loginAsGuest(@Valid @RequestBody GuestAuthRequest request) {
        GuestResponse res = authService.loginAsGuest(request);
        return ResponseEntity.ok(res);
    }




}
