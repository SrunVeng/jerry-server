package com.example.jerryservice.controller;


import com.example.jerryservice.dto.request.MatchCreateRequest;
import com.example.jerryservice.dto.request.UserRegisterRequest;
import com.example.jerryservice.dto.response.MatchCreateResponse;
import com.example.jerryservice.dto.response.MatchDetailResponse;
import com.example.jerryservice.dto.response.UserRegisterResponse;
import com.example.jerryservice.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;


    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER')")
    @PostMapping("/create")
    public ResponseEntity<MatchCreateResponse> create(@Valid @RequestBody MatchCreateRequest request) {
        MatchCreateResponse res = matchService.matchCreate(request);
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_USER')")
    @GetMapping("/getAll")
    public ResponseEntity<List<MatchDetailResponse>> getAll() {
        List<MatchDetailResponse> res = matchService.getAll();
        return ResponseEntity.ok(res);
    }




}
